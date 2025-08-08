package ir.dekot.fileto.feature_create_pdf.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ir.dekot.fileto.feature_create_pdf.data.local.datasource.PdfCreationLocalDataSource
import ir.dekot.fileto.feature_create_pdf.domain.repository.PdfCreationRepository
import ir.dekot.fileto.feature_create_pdf.data.repository.PdfCreationRepositoryImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CreatePdfModule {

    @Provides
    @Singleton
    fun providePdfCreationRepository(
        localDataSource: PdfCreationLocalDataSource
    ): PdfCreationRepository {
        return PdfCreationRepositoryImpl(localDataSource)
    }

    // Hilt می‌داند چگونه PdfCreationLocalDataSource را بسازد چون
    // کانستراکتور آن با @Inject علامت‌گذاری شده و Context را از Hilt می‌گیرد.
}