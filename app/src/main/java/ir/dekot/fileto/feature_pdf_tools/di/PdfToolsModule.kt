package ir.dekot.fileto.feature_pdf_tools.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dekot.fileto.feature_pdf_tools.data.local.datasource.PdfDataSource
import ir.dekot.fileto.feature_pdf_tools.data.local.datasource.PdfDataSourceImpl
import ir.dekot.fileto.feature_pdf_tools.data.repository.PdfToolsRepositoryImpl
import ir.dekot.fileto.feature_pdf_tools.domain.repository.PdfToolsRepository
import javax.inject.Singleton

/**
 * ماژول Hilt برای تزریق وابستگی‌های مربوط به ابزارهای PDF
 * این ماژول مطابق با اصول Clean Architecture طراحی شده است
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class PdfToolsModule {

    /**
     * تزریق DataSource برای عملیات PDF
     */
    @Binds
    @Singleton
    abstract fun bindPdfDataSource(
        pdfDataSourceImpl: PdfDataSourceImpl
    ): PdfDataSource

    /**
     * تزریق Repository برای ابزارهای PDF
     */
    @Binds
    @Singleton
    abstract fun bindPdfToolsRepository(
        pdfToolsRepositoryImpl: PdfToolsRepositoryImpl
    ): PdfToolsRepository
}
