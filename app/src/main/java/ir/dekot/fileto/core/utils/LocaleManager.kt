package ir.dekot.fileto.core.utils

import android.content.Context
import android.content.ContextWrapper
import android.os.LocaleList
import java.util.Locale

fun Context.updateLocale(locale: Locale): ContextWrapper {
    var context = this
    val resources = context.resources
    val configuration = resources.configuration

    val localeList = LocaleList(locale)
    LocaleList.setDefault(localeList)
    configuration.setLocales(localeList)

    context = context.createConfigurationContext(configuration)
    return ContextWrapper(context)
}