package com.example.Mini_Project1.service;

import com.example.Mini_Project1.entity.Voucher;
import com.example.Mini_Project1.exception.BadRequestException;
import com.example.Mini_Project1.exception.NotFoundException;
import com.example.Mini_Project1.repository.VoucherRepository;
import com.example.Mini_Project1.request.voucher.CreateVoucherRequest;
import com.example.Mini_Project1.request.voucher.UpdateVoucherRequest;
import com.example.Mini_Project1.response.voucher.VoucherResponse;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final ModelMapper modelMapper;

    @Transactional
    // create voucher
    public VoucherResponse createVoucherService(CreateVoucherRequest request) {
        if (voucherRepository.existsByCode(request.getCode().trim())) {
            throw new BadRequestException("This voucher has already exist with code " + request.getCode());
        } else {
            modelMapper.getConfiguration().setSkipNullEnabled(true);
            Voucher voucher = modelMapper.map(request, Voucher.class);

            return modelMapper.map(voucherRepository.save(voucher), VoucherResponse.class);
        }
    }

    @Transactional
    // get all voucher
    public List<VoucherResponse> getAllVouchersService() {
        List<Voucher> vouchers = voucherRepository.findAll();
        return modelMapper.map(vouchers, new TypeToken<List<VoucherResponse>>() {
        }.getType());
    }

    @Transactional
    // get by name
    public List<VoucherResponse> getVoucherByNameService(String name) {
        if (name == null) {
            List<Voucher> vouchers = voucherRepository.findAll();
            return modelMapper.map(vouchers, new TypeToken<List<VoucherResponse>>() {
            }.getType());
        }
        List<Voucher> vouchers = voucherRepository.findByName(name);
        return modelMapper.map(vouchers, new TypeToken<List<VoucherResponse>>() {
        }.getType());
    }

    @Transactional
    // get by code
    public VoucherResponse getVoucherByCodeService(String code) {
        Voucher voucher = voucherRepository.findByCode(code.trim()).stream().findFirst().orElseThrow(
                () -> new NotFoundException("Voucher not found with code " + code.trim()));
        return modelMapper.map(voucher, VoucherResponse.class);
    }

    @Transactional
    // update
    public VoucherResponse updateVoucherService(UpdateVoucherRequest request) {
        Voucher voucher = voucherRepository.findById(request.getVoucherId().toString()).orElseThrow(
                () -> new NotFoundException("Voucher not found with id " + request.getVoucherId().toString()));
        if (request.getCode() != null && voucherRepository.existsByCode(request.getCode().trim())) {
            throw new BadRequestException("This voucher has already exist");
        }
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.map(request, voucher);

        return modelMapper.map(voucherRepository.save(voucher), VoucherResponse.class);
    }

    @Transactional
    // delete
    public VoucherResponse deleteVoucherService(UUID voucherId) {
        Voucher voucher = voucherRepository.findById(voucherId.toString())
                .orElseThrow(() -> new NotFoundException("Voucher not found with id " + voucherId.toString()));
        voucherRepository.delete(voucher);
        return modelMapper.map(voucher, VoucherResponse.class);
    }

}
