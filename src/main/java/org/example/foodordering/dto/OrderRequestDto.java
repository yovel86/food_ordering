package org.example.foodordering.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequestDto {

  private List<Long> itemIds;

}
