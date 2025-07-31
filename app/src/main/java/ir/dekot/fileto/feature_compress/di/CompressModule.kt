package ir.dekot.fileto.feature_compress.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ir.dekot.fileto.feature_compress.data.local.datasource.PdfLocalDataSource
import ir.dekot.fileto.feature_compress.data.repository.PdfRepositoryImpl
import ir.dekot.fileto.feature_compress.domain.repository.PdfRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CompressModule {

    @Provides
    @Singleton
    fun providePdfRepository(
        localDataSource: PdfLocalDataSource,
        @ApplicationContext context: Context // اضافه کردن Context به Provider
    ): PdfRepository {
        return PdfRepositoryImpl(localDataSource, context) // پاس دادن Context به پیاده‌سازی
    }

    // Hilt به صورت خودکار می‌داند چگونه PdfLocalDataSource را بسازد
    // چون کانستراکتور آن با @Inject علامت‌گذاری شده و Context را از Hilt می‌گیرد.
}