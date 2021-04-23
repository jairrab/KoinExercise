package com.github.jairrab.koinexercise

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.github.jairrab.koinexercise.databinding.ActivityMainBinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentABinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentBBinding
import com.github.jairrab.koinexercise.databinding.Module1FragmentCBinding
import com.github.jairrab.viewbindingutility.viewBinding
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.android.scope.AndroidScopeComponent
import org.koin.androidx.scope.activityScope
import org.koin.androidx.scope.fragmentScope
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
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
    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    override val scope: Scope by activityScope()

    // Lazy injected MySimplePresenter
    private val presenter by inject<MySimplePresenter>()

    //Lazy inject ViewModel
    private val viewModel by stateViewModel<ActivityViewModel>(
        state = {
            bundleOf("main_activity_args" to "SavedStateHandle initialized w/ MainActivity bundle")
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel.stringArgsLd.observe(this, Observer {
            binding.text1.text = it
        })

        viewModel.helloLd.observe(this, Observer {
            binding.text2.text = it
        })

        binding.text3.isVisible = false
        binding.text3.text = presenter.sayHello()

        binding.button.setOnClickListener {
            viewModel.testProcessDeath()
        }
    }
}

class ActivityViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: HelloRepository,
) : BaseViewModel(savedStateHandle) {
    val stringArgsLd = savedStateHandle.getLiveData<String>("main_activity_args")
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
        stringArgsLd.value = "SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}

class MySimplePresenter() {
    fun sayHello() = "ActivityRetained Scoped Object\n$this"
}
//endregion

//region FRAGMENT A
class Module1FragmentA : BaseFragment(R.layout.module_1_fragment_a), AndroidScopeComponent {
    private val binding by viewBinding { Module1FragmentABinding.bind(it) }

    override val scope: Scope by fragmentScope()
    private val viewModel by stateViewModel<Module1FragmentAViewModel>(
        state = { bundleOf("frag_a_args" to "SavedStateHandle initialized w/ Fragment A bundle") }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stringArgsLd.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            val value = "SavedStateHandle initialized w/ bundle passed from Fragment A"
            val bundle = bundleOf("frag_b_args" to value)
            navigate(R.id.action_module_1_fragment_a_to_module_1_fragment_b, bundle)
        }
    }
}

class Module1FragmentAViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: HelloRepository,
) : BaseViewModel(savedStateHandle) {
    val stringArgsLd = savedStateHandle.getLiveData<String>("frag_a_args")
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
        stringArgsLd.value = "SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region FRAGMENT B
class Module1FragmentB : BaseFragment(R.layout.module_1_fragment_b), AndroidScopeComponent {
    private val binding by viewBinding { Module1FragmentBBinding.bind(it) }

    override val scope: Scope by fragmentScope()
    private val viewModel by stateViewModel<Module1FragmentBViewModel>(
        state = { requireArguments() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stringArgsLd.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            navigate(
                Module1FragmentBDirections.actionModule1FragmentBToModule1FragmentC(
                    data1 = "SavedStateHandle initialized w/ Fragment B bundle"
                )
            )
        }

        binding.button3.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

class Module1FragmentBViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: HelloRepository,
) : BaseViewModel(savedStateHandle) {
    val stringArgsLd = savedStateHandle.getLiveData<String>("frag_b_args")
    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
        stringArgsLd.value = "SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region FRAGMENT C
class Module1FragmentC : BaseFragment(R.layout.module_1_fragment_c), AndroidScopeComponent {
    private val binding by viewBinding { Module1FragmentCBinding.bind(it) }

    override val scope: Scope by fragmentScope()
    private val viewModel by stateViewModel<Module1FragmentCViewModel>(
        state = { requireArguments() }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.stringArgsLd.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })

        viewModel.helloLd.observe(viewLifecycleOwner, Observer {
            binding.text2.text = it
        })

        binding.button1.setOnClickListener {
            viewModel.testProcessDeath()
        }

        binding.button2.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

class Module1FragmentCViewModel(
    savedStateHandle: SavedStateHandle,
    private val repo: HelloRepository,
) : BaseViewModel(savedStateHandle) {
    private val args: Module1FragmentCArgs by navArgs()

    val stringArgsLd = savedStateHandle.getLiveData("some_string", args.data1)

    val helloLd = MutableLiveData<String>()

    init {
        Log.v("koin_test", "Initializing $this")
    }

    fun testProcessDeath() {
        val giveHello = repo.giveHello()
        helloLd.value = "NON-SAVED STATE\n$giveHello"
        stringArgsLd.value = "SAVED STATE\n$giveHello"
    }

    override fun onCleared() {
        super.onCleared()
        Log.v("koin_test", "Cleared $this")
    }
}
//endregion

//region REPOSITORY
interface HelloRepository {
    fun giveHello(): String
}

class HelloRepositoryImpl() : HelloRepository {
    override fun giveHello() = "Hello Koin from $this"
}
//endregion

//region KOIN MODULES
object Modules {
    val appModule = module {
        //factory - to produce a new instance each time the by inject() or get() is called
        //factory { MySimplePresenter(get()) }

        //scope - to produce an instance tied to a scope
        scope<MainActivity> {
            scoped { MySimplePresenter() }
        }

        // single instance of HelloRepository
        single<HelloRepository> { HelloRepositoryImpl() }

        viewModel { ActivityViewModel(get(), get()) }

        scope<Module1FragmentA> {
            scoped { Module1FragmentAViewModel(get(), get()) }
        }

        scope<Module1FragmentB> {
            scoped { Module1FragmentBViewModel(get(), get()) }
        }

        scope<Module1FragmentC> {
            scoped { Module1FragmentCViewModel(get(), get()) }
        }
    }
}
//endregion