package com.github.jairrab.koinexercise

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module

//region APPLICATION
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Start Koin
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(Modules.appModule)
        }
    }
}
//endregion

//region MAIN ACTIVITY
class MainActivity : AppCompatActivity(), AndroidScopeComponent {
    override val scope: Scope by activityScope()

    // Lazy injected MySimplePresenter
    private val firstPresenter: MySimplePresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.v("koin_test", firstPresenter.sayHello())

        findViewById<TextView>(R.id.text).text = firstPresenter.sayHello()
    }
}
//endregion

class MyViewModel : ViewModel() {

}

//region PRESENTER
class MySimplePresenter(private val repo: HelloRepository) {
    fun sayHello() = "${repo.giveHello()} from $this"
}
//endregion

//region REPOSITORY
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin"
}
//endregion

//region KOIN MODULES
object Modules {
    val appModule = module {
        // single instance of HelloRepository
        single<HelloRepository> { HelloRepositoryImpl() }

        //factory - to produce a new instance each time the by inject() or get() is called
        //factory { MySimplePresenter(get()) }

        //scope - to produce an instance tied to a scope
        scope<MainActivity> {
            scoped { MySimplePresenter(get()) }
        }
    }
}
//endregion