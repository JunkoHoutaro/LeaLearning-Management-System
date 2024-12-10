package com.example.Mini_Project1.controller;

import com.example.Mini_Project1.request.voucher.CreateVoucherRequest;
import com.example.Mini_Project1.request.voucher.UpdateVoucherRequest;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import com.example.Mini_Project1.service.VoucherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/vouchers")
@AllArgsConstructor
public class VoucherController {

    private VoucherService voucherService;

    // create voucher
    @PostMapping("insert")
    @Operation(summary = "Create a new voucher")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<VoucherResponse>  createVoucher(@Valid @RequestBody CreateVoucherRequest request){
        return ResponseEntity.ok(voucherService.createVoucherService(request));
    }

    // get all voucher
    @GetMapping("get-all-vouchers")
    @Operation(summary = "Get all vouchers")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<VoucherResponse>> getAllVouchers() {
        return ResponseEntity.ok(voucherService.getAllVouchersService());
    }

    // get voucher by name
    @GetMapping("get-by-name")
    @Operation(summary = "Get voucher by name")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<VoucherResponse>> getVoucherByName(@RequestParam String name){
        return ResponseEntity.ok(voucherService.getVoucherByNameService(name));
    }

    // get voucher by code
    @GetMapping("get-by-code")
    @Operation(summary = "Get voucher by code")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public  ResponseEntity<List<VoucherResponse>> getVoucherByCode(@RequestParam String code){
        return ResponseEntity.ok(voucherService.getVoucherByCodeService(code));
    }

    //update
    @PatchMapping("update")
    @Operation(summary = "Update  a voucher")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<VoucherResponse> updateVoucher(@Valid @RequestBody UpdateVoucherRequest request) {
        return ResponseEntity.ok(voucherService.updateVoucherService(request));
    }

    // delete
    @DeleteMapping("delete")
    @Operation(summary = "Delete a voucher")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<VoucherResponse> deleteVoucher(@RequestParam UUID voucherId) {
        return ResponseEntity.ok(voucherService.deleteVoucherService(voucherId));
    }
}
