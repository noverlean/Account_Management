package com.example.account_management.mapper;

import com.example.account_management.entity.Account;
import com.example.account_management.entity.User;
import org.mapstruct.Mapper;
import org.openapitools.model.AccountDto;
import org.openapitools.model.UserDto;
import org.springframework.stereotype.Component;

@Mapper(componentModel = "spring")
@Component
public interface AccountMapper {

    Account dtoToModel(AccountDto user);

//    @Mapping(target = "status", ignore = true)
//    @Mapping(target = "userId", source = "id")
    AccountDto modelToDto(Account user);
}
