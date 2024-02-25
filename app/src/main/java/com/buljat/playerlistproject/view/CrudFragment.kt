package com.buljat.playerlistproject.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.buljat.playerlistproject.databinding.FragmentCrudBinding
import com.bumptech.glide.Glide
import com.buljat.playerlistproject.R
import com.buljat.playerlistproject.model.PlayerDbEntity
import com.buljat.playerlistproject.viewmodel.PlayersListViewModel
import com.buljat.playerlistproject.viewmodel.PlayersListViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


class CrudFragment : Fragment() {

    private val args: CrudFragmentArgs by navArgs()

    private val viewModel: PlayersListViewModel by viewModels {
        PlayersListViewModelFactory(requireActivity().application)
    }

    private var _binding: FragmentCrudBinding? = null
    private val binding get() = _binding!!

    private var picturePath: String? = null
    private var selectedDate: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCrudBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPlayer(args.playerId)
        Log.i("XXX", "${args.playerId}")

        viewModel.player.observe(viewLifecycleOwner) {
            assignData(viewModel.player)
        }
        activateButtons()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            1 -> {
                if (resultCode == Activity.RESULT_OK) {
                    picturePath?.let { loadTakenImage(it) }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                val allPermissionsGranted =
                    grantResults.all { it == PackageManager.PERMISSION_GRANTED }

                if (allPermissionsGranted) {
                    takePicture()
                } else {
//                    showPermissionDeniedDialog()
                    Toast.makeText(
                        requireContext(),
                        "Permission denied!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            else -> {
                // Ignore
            }
        }
    }

    private fun activateButtons() = with(binding) {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        datePicker.init(
            year,
            month,
            day,
            DatePicker.OnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDateCalendar = Calendar.getInstance()
                selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)

                selectedDate = selectedDateCalendar.time
            })

        btnInsert.setOnClickListener {
            if (args.playerId != -1) {
                viewModel.updatePlayer(inputExistingPlayer())
            } else {
                viewModel.insertPlayer(inputNewPlayer())
            }
            findNavController().navigateUp()
        }

        btnDelete.setOnClickListener {
            viewModel.deletePlayer()
            findNavController().navigateUp()
        }

        btnTakePicture.setOnClickListener {
            checkPermission()
        }
    }

    private fun checkPermission() = when {
        ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
            takePicture()
        }

        else -> {
            val cameraPermission = Manifest.permission.CAMERA
            val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE

            val hasCameraPermission = ContextCompat.checkSelfPermission(
                requireContext(),
                cameraPermission
            ) == PackageManager.PERMISSION_GRANTED
            val hasStoragePermission = ContextCompat.checkSelfPermission(
                requireContext(),
                storagePermission
            ) == PackageManager.PERMISSION_GRANTED

            val permissionsToRequest = mutableListOf<String>()
            if (!hasCameraPermission) {
                permissionsToRequest.add(cameraPermission)
            }
            if (!hasStoragePermission) {
                permissionsToRequest.add(storagePermission)
            }
            requestPermissions(permissionsToRequest.toTypedArray(), 2)
        }
    }

    private fun createPictureFile(): String? {
        val name = SimpleDateFormat("yyyyMMddHHmmss").format(Date()) + "_camera"
        val dir: File? = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile(name, ".jpg", dir)
        return file.path
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(activity?.packageManager ?: return) == null) {
            return
        }

        picturePath = createPictureFile()

        if (picturePath == null) {
            return
        }

        val pictureFile = File(picturePath)
        val pictureUri: Uri = FileProvider.getUriForFile(
            requireActivity(),
            "buljat.playerlistproject.provider",
            pictureFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, pictureUri)
        startActivityForResult(intent, 1)
    }

    private fun loadTakenImage(imageUri: String) = with(binding) {
        Glide
            .with(root)
            .load(imageUri)
            .into(pictureTaken)
    }

    private fun inputNewPlayer(): PlayerDbEntity = PlayerDbEntity(
        name = binding.etPlayerName.text.toString(),
        sport = binding.etSport.text.toString(),
        image = picturePath ?: "",
        birthDate = selectedDate,
        gender = view?.findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId)?.text.toString()
    )

    private fun inputExistingPlayer(): PlayerDbEntity = PlayerDbEntity(
        id = viewModel.player.value!!.id,
        name = binding.etPlayerName.text.toString(),
        sport = binding.etSport.text.toString(),
        image = picturePath ?: "",
        birthDate = selectedDate,
        gender = view?.findViewById<RadioButton>(binding.rgGender.checkedRadioButtonId)?.text.toString()
    )

    private fun assignData(cameraLiveData: LiveData<PlayerDbEntity?>) {
        cameraLiveData.value?.let { player ->
            with(binding) {
                etPlayerName.setText(player.name)
                etSport.setText(player.sport)
                when (player.gender) {
                    "Male" -> rgGender.check(R.id.rb_male)
                    "Female" -> rgGender.check(R.id.rb_female)
                }
                Glide
                    .with(root)
                    .load(player.image)
                    .into(pictureTaken)

                val calendar = Calendar.getInstance()

                if (viewModel.player.value?.birthDate != null) {
                    selectedDate = viewModel.player.value?.birthDate!!
                    calendar.time = selectedDate!!
                } else selectedDate = calendar.time
                val year = calendar.get(Calendar.YEAR)
                val month = calendar.get(Calendar.MONTH)
                val day = calendar.get(Calendar.DAY_OF_MONTH)

                datePicker.init(
                    year,
                    month,
                    day,
                    DatePicker.OnDateChangedListener { _, selectedYear, selectedMonth, selectedDay ->
                        val selectedDateCalendar = Calendar.getInstance()
                        selectedDateCalendar.set(selectedYear, selectedMonth, selectedDay)

                        selectedDate = selectedDateCalendar.time
                    })
            }
        }
    }
}