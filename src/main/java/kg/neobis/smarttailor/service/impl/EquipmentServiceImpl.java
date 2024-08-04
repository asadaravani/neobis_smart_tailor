package kg.neobis.smarttailor.service.impl;

import com.cloudinary.utils.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Phrase;
import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.entity.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kg.neobis.smarttailor.dtos.ads.detailed.EquipmentDetailed;
import kg.neobis.smarttailor.dtos.ads.list.EquipmentListDto;
import kg.neobis.smarttailor.dtos.ads.request.EquipmentRequestDto;
import kg.neobis.smarttailor.exception.*;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.repository.EquipmentRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EquipmentServiceImpl implements EquipmentService {

    AppUserService appUserService;
    EquipmentRepository equipmentRepository;
    EquipmentMapper equipmentMapper;
    ObjectMapper objectMapper;
    EmailService emailService;
    Validator validator;
    CloudinaryService cloudinaryService;

    @Override
    public String addEquipment(String equipmentRequestDto, List<MultipartFile> images, Authentication authentication) {

        EquipmentRequestDto requestDto = parseAndValidateRecipeDto(equipmentRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        List<Image> equipmentImages = cloudinaryService.saveImages(images);

        Equipment equipment = equipmentMapper.dtoToEntity(requestDto, equipmentImages, user);
        equipmentRepository.save(equipment);
        return "Equipment has been created";
    }

    @Override
    @Transactional
    public String deleteEquipment(Long equipmentId) throws IOException {

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        for (Image image : equipment.getImages()) {
            cloudinaryService.deleteImage(image.getUrl());
        }

        equipmentRepository.delete(equipment);
        return "Equipment has been deleted";
    }

    @Override
    public List<EquipmentListDto> getAllEquipments(int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<Equipment> equipments = equipmentRepository.findByIsVisible(true, pageable);
        List<Equipment> equipmentList = equipments.getContent();
        return equipmentMapper.entityListToDtoList(equipmentList);
    }

    @Override
    public EquipmentDetailed getEquipmentById(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));
        return equipmentMapper.entityToDto(equipment);
    }

    @Override
    public String hideEquipment(Long equipmentId, Authentication authentication) {

        AppUser user = appUserService.getUserFromAuthentication(authentication);
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        if (!equipment.getIsVisible()) {
            throw new ResourceAlreadyExistsException("Equipment is already hidden");
        }
        if (!equipment.getAuthor().getId().equals(user.getId())) {
            throw new NoPermissionException("Only authors can hide their equipments");
        }
        equipment.setIsVisible(false);
        equipmentRepository.save(equipment);

        return "Equipment is now invisible in marketplace";
    }

    private EquipmentRequestDto parseAndValidateRecipeDto(String equipmentDto) {
        try {
            EquipmentRequestDto requestDto = objectMapper.readValue(equipmentDto, EquipmentRequestDto.class);

            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "equipmentRequestDto");
            validator.validate(equipmentDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            if (StringUtils.isBlank(requestDto.name())) {
                throw new InvalidRequestException("Name cannot be empty");
            }
            if (StringUtils.isBlank(requestDto.description())) {
                throw new InvalidRequestException("Description cannot be empty");
            }
            if (requestDto.price() == null || requestDto.price().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidRequestException("Price must be greater than zero");
            }
            if (StringUtils.isBlank(requestDto.contactInfo())) {
                throw new InvalidRequestException("Contact info cannot be empty");
            }
            if (requestDto.quantity() == null || requestDto.quantity() <= 0) {
                throw new InvalidRequestException("Quantity must be greater than zero");
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage());
        }
    }

    @Override
    public String buyEquipment(Long equipmentId, Authentication authentication) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found"));

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (equipment.getQuantity() == null || equipment.getQuantity() <= 0) {
            throw new OutOfStockException("This equipment is out of stock");
        }

        byte[] pdfFile;
        try {
            pdfFile = generateReceiptPdf(equipment, user);
            emailService.sendEmailWithReceiptPDF(user, pdfFile);
        } catch (IOException | MessagingException exception) {
            throw new IllegalStateException("Something went wrong! Please, try again!");
        }

        equipment.setQuantity(equipment.getQuantity() - 1);
        equipmentRepository.save(equipment);
        return "You have successfully purchased the equipment. Receipt sent to the email. Please check your email";
    }

    @Override
    public List<Equipment> findAllByUser(AppUser user) {
        return equipmentRepository.findAllByAuthor(user);
    }

    @Override
    public List<EquipmentListDto> searchEquipments(String name, Authentication authentication) {
        appUserService.getUserFromAuthentication(authentication);
        List<Equipment> equipmentList = equipmentRepository.findEquipmentByNameContainingIgnoreCase(name);
        return equipmentMapper.entityListToDtoList(equipmentList);
    }

    private void addTableCell(PdfPTable table, String text, Font font, BaseColor borderColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorderColor(borderColor);
        cell.setPadding(10f);
        table.addCell(cell);
    }

    private byte[] generateReceiptPdf(Equipment equipment, AppUser user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD, BaseColor.DARK_GRAY);
            Paragraph title = new Paragraph("Receipt", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20f);
            document.add(title);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1, 2});

            Font headerFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.WHITE);
            Font cellFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            BaseColor headerColor = new BaseColor(79, 129, 189);
            BaseColor borderColor = BaseColor.LIGHT_GRAY;

            PdfPCell cell1 = new PdfPCell(new Phrase("Field", headerFont));
            PdfPCell cell2 = new PdfPCell(new Phrase("Details", headerFont));
            cell1.setBackgroundColor(headerColor);
            cell2.setBackgroundColor(headerColor);
            cell1.setBorderColor(borderColor);
            cell2.setBorderColor(borderColor);
            cell1.setPadding(10f);
            cell2.setPadding(10f);
            table.addCell(cell1);
            table.addCell(cell2);

            addTableCell(table, "Date", cellFont, borderColor);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            String formattedDate = dateFormat.format(date);
            addTableCell(table, formattedDate, cellFont, borderColor);

            addTableCell(table, "Buyer", cellFont, borderColor);
            addTableCell(table, user.getName() + " " + user.getSurname(), cellFont, borderColor);

            addTableCell(table, "Equipment", cellFont, borderColor);
            addTableCell(table, equipment.getName(), cellFont, borderColor);

            addTableCell(table, "Price", cellFont, borderColor);
            addTableCell(table, "$" + equipment.getPrice(), cellFont, borderColor);

            document.add(table);
            document.close();
        } catch (DocumentException exception) {
            throw new PdfGenerationException(exception.getMessage());
        }
        return out.toByteArray();
    }
}