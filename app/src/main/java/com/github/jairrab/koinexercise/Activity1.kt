package com.github.jairrab.koinexercise

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.github.jairrab.koinexercise.databinding.*
import com.github.jairrab.viewbindingutility.viewBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.scope.activityRetainedScope
import org.koin.androidx.viewmodel.ViewModelOwner
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.stateViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.KoinScopeComponent
import org.koin.core.scope.Scope
import org.koin.dsl.module

//endregion

//region MAIN ACTIVITY
class Activity1 : AppCompatActivity(), KoinScopeComponent {
    private val binding by viewBinding { ActivityMainBinding.inflate(it) }
    override val scope: Scope by activityRetainedScope()

    // Lazy injected MySimplePresenter
    private val presenter:MySimplePresenter by inject()

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

        //binding.text3.isVisible = false
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
    fun sayHello() = "Hello from $this"
}
//endregion

//region FRAGMENT A
class Module1FragmentA : BaseFragment(R.layout.module_1_fragment_a) {
    private val binding by viewBinding { Module1FragmentABinding.bind(it) }

    private val viewModel by stateViewModel<Module1FragmentAViewModel>(
        state = { bundleOf("frag_a_args" to "SavedStateHandle initialized w/ Fragment A bundle") }
    )

    private val mySimplePresenter: MySimplePresenter by lazy {
        (requireActivity() as KoinScopeComponent).scope.get()
    }

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

        val hello = mySimplePresenter.sayHello()
        Log.v("koin_test", "$hello")
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
class Module1FragmentB : BaseFragment(R.layout.module_1_fragment_b) {
    private val binding by viewBinding { Module1FragmentBBinding.bind(it) }

    //override val scope: Scope by fragmentScope()
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
class Module1FragmentC : BaseFragment(R.layout.module_1_fragment_c) {
    private val binding by viewBinding { Module1FragmentCBinding.bind(it) }

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
            navigate(R.id.action_module_1_fragment_c_to_module_1_fragment_d)
        }

        binding.button3.setOnClickListener {
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

//region FRAGMENT D
class Module1FragmentD : BaseFragment(R.layout.module_1_fragment_d) {
    private val binding by viewBinding { Module1FragmentDBinding.bind(it) }
    private val viewModel by viewModel<Module1FragmentDViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })

        childFragmentManager.beginTransaction().let { transaction ->
            transaction.add(R.id.fragment_1, Module1FragmentDChildA())
            transaction.commit()
        }

        childFragmentManager.beginTransaction().let { transaction ->
            transaction.add(R.id.fragment_2, Module1FragmentDChildB())
            transaction.commit()
        }

        binding.button1.setOnClickListener {
            findNavController().popBackStack()
        }
    }
}

class Module1FragmentDChildA : BaseFragment(R.layout.module_1_fragment_d_child_a) {
    private val binding by viewBinding { Module1FragmentDChildABinding.bind(it) }
    private val viewModel by sharedViewModel<Module1FragmentDViewModel>(
        owner = { ViewModelOwner.from(requireParentFragment(), requireParentFragment()) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })
    }
}

class Module1FragmentDChildB : BaseFragment(R.layout.module_1_fragment_d_child_b) {
    private val binding by viewBinding { Module1FragmentDChildBBinding.bind(it) }
    private val viewModel by sharedViewModel<Module1FragmentDViewModel>(
        owner = { ViewModelOwner.from(requireParentFragment(), requireParentFragment()) }
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.data.observe(viewLifecycleOwner, Observer {
            binding.text1.text = it
        })
    }
}

class Module1FragmentDViewModel(
) : ViewModel() {

    val data = liveData {
        emit("Hello from $this")
    }

    init {
        Log.v("koin_test", "Initializing $this")
    }

    override fun onCleared() {
        Log.v("koin_test", "Cleared $this")
        super.onCleared()
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
        scope<Activity1> {
            scoped { MySimplePresenter() }
        }

        // single instance of HelloRepository
        single<HelloRepository> { HelloRepositoryImpl() }

        viewModel { ActivityViewModel(get(), get()) }

        viewModel { Module1FragmentAViewModel(get(), get()) }

        viewModel { Module1FragmentBViewModel(get(), get()) }

        viewModel { Module1FragmentCViewModel(get(), get()) }

        viewModel { Module1FragmentDViewModel() }

        /*scope<Module1FragmentD> {
            viewModel { Module1FragmentDViewModel() }
        }

        scope<Module1FragmentDChildA> {
            viewModel { Module1FragmentDViewModel() }
        }

        scope<Module1FragmentDChildB> {
            viewModel { Module1FragmentDViewModel() }
        }*/

        /*scope<Module1FragmentDChildA> {
            scoped { Module1FragmentDViewModel(get()) }
        }

        scope<Module1FragmentDChildB> {
            scoped { Module1FragmentDViewModel(get()) }
        }*/
    }
}
//endregion