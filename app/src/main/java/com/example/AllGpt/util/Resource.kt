package com.example.AllGpt.util

sealed class Resource<out T> {
    data class Success<out T>(val data : T) :  Resource<T>()
    data class Error(val message : String?) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}


//best practice instead boolean;
//<T> berarti lebih terperinci; misalnya ada data baru yang didapat. <nothing> ketika tidak perlu data tambahan
//seperti error / loading