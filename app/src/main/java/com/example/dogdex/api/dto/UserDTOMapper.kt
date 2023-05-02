package com.example.dogdex.api.dto

import com.example.dogdex.model.User

class UserDTOMapper {

    fun fromUserDTOToUserDomain(userDTO: UserDTO): User =
        User(
            id = userDTO.id,
            email = userDTO.email,
            authenticationToken = userDTO.authenticationToken
        )
}