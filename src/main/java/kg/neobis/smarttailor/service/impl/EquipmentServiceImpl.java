package kg.neobis.smarttailor.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import jakarta.mail.MessagingException;
import kg.neobis.smarttailor.entity.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import kg.neobis.smarttailor.dtos.EquipmentDto;
import kg.neobis.smarttailor.dtos.EquipmentListDto;
import kg.neobis.smarttailor.dtos.EquipmentRequestDto;
import kg.neobis.smarttailor.entity.AppUser;
import kg.neobis.smarttailor.entity.Equipment;
import kg.neobis.smarttailor.exception.InvalidJsonException;
import kg.neobis.smarttailor.exception.OutOfStockException;
import kg.neobis.smarttailor.exception.PdfGenerationException;
import kg.neobis.smarttailor.exception.ResourceNotFoundException;
import kg.neobis.smarttailor.mapper.EquipmentMapper;
import kg.neobis.smarttailor.repository.EquipmentRepository;
import kg.neobis.smarttailor.service.AppUserService;
import kg.neobis.smarttailor.service.CloudinaryService;
import kg.neobis.smarttailor.service.EmailService;
import kg.neobis.smarttailor.service.EquipmentService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    public List<EquipmentListDto> getAllEquipments() {
        List<Equipment> equipmentList = equipmentRepository.findAll();
        return equipmentMapper.entityListToDtoList(equipmentList);
    }

    @Override
    public EquipmentDto getEquipmentById(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId).orElseThrow(() -> new ResourceNotFoundException("Equipment not found", HttpStatus.NOT_FOUND));
        return equipmentMapper.entityToDto(equipment);
    }

    @Override
    public String addEquipment(String equipmentRequestDto, List<MultipartFile> images, Authentication authentication) {

        EquipmentRequestDto requestDto = parseAndValidateRecipeDto(equipmentRequestDto);
        AppUser user = appUserService.getUserFromAuthentication(authentication);
        List<Image> equipmentImages = cloudinaryService.saveImages(images);

        Equipment equipment = equipmentMapper.dtoToEntity(requestDto, equipmentImages, user);
        equipmentRepository.save(equipment);
        return "The equipment has been added successfully!";
    }

    private EquipmentRequestDto parseAndValidateRecipeDto(String equipmentDto) {
        try {
            EquipmentRequestDto requestDto = objectMapper.readValue(equipmentDto, EquipmentRequestDto.class);

            BindingResult bindingResult = new BeanPropertyBindingResult(requestDto, "equipmentRequestDto");
            validator.validate(equipmentDto, bindingResult);
            if (bindingResult.hasErrors()) {
                throw new IllegalArgumentException("Invalid input " + bindingResult.getAllErrors());
            }
            return requestDto;
        } catch (JsonProcessingException e) {
            throw new InvalidJsonException(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @Override
    public String buyEquipment(Long equipmentId, Authentication authentication) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found", HttpStatus.NOT_FOUND));

        AppUser user = appUserService.getUserFromAuthentication(authentication);

        if (equipment.getQuantity() <= 0) {
            throw new OutOfStockException("This equipment is out of stock", HttpStatus.NOT_FOUND);
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
        return "Receipt sent to the email successfully";
    }


    private byte[] generateReceiptPdf(Equipment equipment, AppUser user) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, out);
            document.open();
            document.add(new Paragraph("Receipt"));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            PdfPCell cell1 = new PdfPCell(new Paragraph("Field"));
            PdfPCell cell2 = new PdfPCell(new Paragraph("Details"));
            cell1.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell2.setBackgroundColor(BaseColor.LIGHT_GRAY);
            table.addCell(cell1);
            table.addCell(cell2);

            table.addCell("Date");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            String formattedDate = dateFormat.format(date);
            table.addCell(formattedDate);
            table.addCell("Buyer");
            table.addCell(user.getName() + " " + user.getSurname());
            table.addCell("Equipment");
            table.addCell(equipment.getName());
            table.addCell("Price");
            table.addCell("$" + equipment.getPrice());
            document.add(table);
            document.close();
        } catch (DocumentException exception) {
            throw new PdfGenerationException(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return out.toByteArray();
    }
}