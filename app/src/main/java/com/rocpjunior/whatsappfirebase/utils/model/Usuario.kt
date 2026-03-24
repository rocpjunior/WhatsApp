package com.rocpjunior.whatsappfirebase.utils.model

data class Usuario(
    var id: String,
    var nome: String,
    var email: String,
    var foto: String = ""
)
