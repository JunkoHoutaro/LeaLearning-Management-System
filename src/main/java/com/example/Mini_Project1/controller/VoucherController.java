package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.exception.ErrorResponse;
import com.example.Mini_Project1.request.voucher.CreateVoucherRequest;
import com.example.Mini_Project1.request.voucher.UpdateVoucherRequest;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import com.example.Mini_Project1.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/vouchers")
@AllArgsConstructor
public class VoucherController {

    private VoucherService voucherService;

    // create voucher
    @PostMapping(value = "insert", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new voucher")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<VoucherResponse>  createVoucher(@Valid @RequestBody CreateVoucherRequest request){
        return ResponseEntity.ok(voucherService.createVoucherService(request));
    }

    // get all voucher
    @GetMapping(value = "get-all-vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all vouchers")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<VoucherResponse>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchersService());
    }

    // get voucher by name
    @GetMapping(value = "get-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get voucher by name")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<VoucherResponse>> getVoucherByName(@RequestParam(required = false) String name){
        return ResponseEntity.ok(voucherService.getVoucherByNameService(name));
    }

    // get voucher by code
    @GetMapping(value = "get-by-code", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get voucher by code")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<VoucherResponse> getVoucherByCode(@RequestParam String code){
        return ResponseEntity.ok(voucherService.getVoucherByCodeService(code));
    }

    //update
    @PatchMapping(value = "update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update  a voucher")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<VoucherResponse> updateVoucher(@Valid @RequestBody UpdateVoucherRequest request) {
        return ResponseEntity.ok(voucherService.updateVoucherService(request));
    }

    // delete
    @DeleteMapping(value = "delete", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a voucher")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<VoucherResponse> deleteVoucher(@RequestParam UUID voucherId) {
        return ResponseEntity.ok(voucherService.deleteVoucherService(voucherId));
    }
}
