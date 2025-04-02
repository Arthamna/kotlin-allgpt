package com.example.AllGpt.di

//import com.example.whatsapp.data.database.UserDataBase
import android.content.Context
import androidx.room.Room
import com.example.AllGpt.data.database.AppDatabase
import com.example.AllGpt.data.database.MessageDao
import com.example.AllGpt.data.database.TopicDao
import com.example.AllGpt.data.database.UserDao
import com.example.AllGpt.data.repository.AuthRepositoryImpl
import com.example.AllGpt.data.repository.ChatRepositoryImpl
import com.example.AllGpt.data.repository.LLMRepository
import com.example.AllGpt.domain.repository.AuthRepository
import com.example.AllGpt.domain.repository.ChatRepository
import com.example.AllGpt.domain.use_case.AuthenticationUseCase
import com.example.AllGpt.domain.use_case.ChatUseCase
import com.example.AllGpt.util.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AllGptModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "all_gpt_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideUserDao(appDatabase: AppDatabase): UserDao {
        return appDatabase.userDao()
    }

    @Provides
    @Singleton
    fun provideTopicDao(appDatabase: AppDatabase): TopicDao {
        return appDatabase.topicDao()
    }

    @Provides
    @Singleton
    fun provideMessageDao(appDatabase: AppDatabase): MessageDao {
        return appDatabase.messageDao()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        userDao: UserDao
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firebaseFirestore, userDao)
    }


    @Provides
    @Singleton
    fun provideAuthenticationUseCase(authRepository: AuthRepository): AuthenticationUseCase {
        return AuthenticationUseCase(authRepository)
    }

    @Provides
    @Singleton
    fun provideChatRepository( auth: FirebaseAuth, database: AppDatabase, llmModel: LLMRepository
    ): ChatRepository {
        return ChatRepositoryImpl(auth, database, llmModel)
    }

    @Provides
    @Singleton
    fun provideChatUseCase(chatRepository: ChatRepository): ChatUseCase {
        return ChatUseCase(chatRepository)
    }

//    @Singleton
//    @Provides
//    fun provideUserRepository(
//        firestore: FirebaseFirestore,
//        userDao: UserDao,
//    ): UserRepository {
//        return UserRepositoryImpl(firestore, userDao)
//    }

//    @Provides
//    @Singleton
//    fun provideContactsUseCase(repository: UserRepository): ContactsUseCase {
//        return ContactsUseCase(repository)
//    }

}