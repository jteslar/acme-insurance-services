package com.acme.services.commons.utils

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

// can be app parameters
private const val LOCALE = "de"
private const val DATE_FORMAT = "dd.MM.yy"

fun String.toNumber(): Long = NumberFormat.getNumberInstance(Locale(LOCALE)).parse(this).toLong()
fun String.toUnlimitedNumber(): Long? = if (this == "unlimited") null else toNumber()
fun String.toPercentage(): Double = NumberFormat.getNumberInstance(Locale(LOCALE)).parse(this.replace("%", "")).toDouble()
fun String.toDate(): LocalDate = LocalDate.parse(this,  DateTimeFormatter.ofPattern(DATE_FORMAT))