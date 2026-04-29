package com.accenture.franchise.dto;

import com.accenture.franchise.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductPerBranchResponse {
    private String branchName;
    private Product product;
}
