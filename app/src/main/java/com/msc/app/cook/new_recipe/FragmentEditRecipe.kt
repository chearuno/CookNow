package com.msc.app.cook.new_recipe

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.msc.app.cook.ImagesActivity
import com.msc.app.cook.ImagesActivity.selectedImageList
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.SelectedImageAdapter
import com.msc.app.cook.utils.CustomDialog
import com.msc.app.cook.utils.Utils.alerterDialog
import com.msc.app.cook.utils.Utils.toastError
import kotlinx.android.synthetic.main.fragment_new_recipe.view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.contentView
import java.text.SimpleDateFormat
import java.util.*


class FragmentEditRecipe : Fragment() {

    var selectedImageRecyclerView: RecyclerView? = null
    var selectedImageAdapter: SelectedImageAdapter? = null

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_recipe, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "EDIT RECIPE"

        selectedImageRecyclerView = view.findViewById(R.id.selected_recycler_view)

        view.btn_next.setOnClickListener {
            when {
                selectedImageList.isEmpty() -> {
                    alerterDialog("Error!", "Please select an images.", requireActivity())
                }
                view.recipe_name.text.isEmpty() -> {
                    alerterDialog("Error!", "Recipe name should not be empty.", requireActivity())
                }
                view.recipe_description.text.isEmpty() -> {
                    alerterDialog(
                        "Error!",
                        "Recipe description should not be empty.",
                        requireActivity()
                    )
                }
                else -> {
                    val imm =
                        requireActivity().getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
                    imm?.hideSoftInputFromWindow(view.windowToken, 0)

                    val prefs: SharedPreferences =
                        requireActivity().getSharedPreferences("MyPrefsFile", 0)
                    val userId = prefs.getString("loggedUserId", "").toString()
                    val firstName = prefs.getString("loggedUserFirstName", "").toString()
                    val lastName = prefs.getString("loggedUserLastName", "").toString()

                    val putData: HashMap<String, Any> = HashMap()
                    putData["name"] = view.recipe_name.text.toString()
                    putData["likes"] = 0
                    putData["userId"] = userId.toInt()
                    putData["createdBy"] = firstName + lastName
                    putData["description"] = view.recipe_description.text.toString()
                    putData["isPrivate"] = view.checkBox.isChecked
                    putData["prepare_time"] = view.txt_cooling_t.text.toString()
                    putData["createdDate"] =
                        SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)

                    val tempFragment = FragmentEditIngredients()
                    val bundle = Bundle()
                    bundle.putSerializable("RECIPE_DATA", putData)
                    tempFragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.frameLayout, tempFragment).addToBackStack(null)
                        .commit()
                }
            }
        }

        view.layout_cooling_t5.setOnClickListener {

            displayCustomT5Dialog(view)
        }

        view.choose_image.setOnClickListener {

            val intent = Intent(activity, ImagesActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun setSelectedImageList() {
        if (selectedImageList.isEmpty() && view != null) {
            requireView().recipe_image_layout.visibility = View.VISIBLE
            requireView().recipe_image.visibility = View.VISIBLE
            selectedImageRecyclerView?.visibility = View.GONE
            requireView().recipe_image.setBackgroundResource(R.drawable.search_empty)
            requireView().choose_image.text = "Select an images"

        } else {
            requireView().recipe_image.visibility = View.GONE
            view?.recipe_image_layout?.visibility = View.GONE
            selectedImageRecyclerView?.visibility = View.VISIBLE
            requireView().choose_image.text = "Change selected Images"

            val layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            selectedImageRecyclerView?.layoutManager = layoutManager
            selectedImageAdapter = SelectedImageAdapter(requireContext(), selectedImageList)
            selectedImageRecyclerView?.adapter = selectedImageAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        setSelectedImageList()
    }

    fun displayCustomT5Dialog(view: View) {
        val t5DialogUi by lazy {
            requireActivity().contentView?.let {
                CustomDialog(AnkoContext.create(requireContext(), it))

            }
        }

        t5DialogUi?.okButton?.setOnClickListener {
            val minituesVal = t5DialogUi!!.minInputText.text.toString()
            val secondVal = t5DialogUi!!.secInputText.text.toString()

            var totalSec = 0
            var min = 0
            var sec = 0
            var errorSec = false


            if (secondVal.equals("") && minituesVal.equals("")) {
                toastError("Please enter time before proceed", requireContext())
            } else {
                if (secondVal.equals("")) {
                    sec = 0
                } else {
                    if (secondVal.toInt() > 60) {
                        toastError(
                            "Invalid Seconds. Please enter below 60 value for seconds",
                            requireContext()
                        )
                        errorSec = true
                    } else {
                        sec = secondVal.toInt()
                    }
                }

                if (minituesVal.equals("")) {
                    min = 0
                } else {
                    min = minituesVal.toInt()
                }
                totalSec = min + sec

                if (totalSec >= 0 && !errorSec) {
                    view.txt_cooling_t.text = "$min min $sec sec"
                    t5DialogUi?.dialog?.dismiss()
                } else {
                    toastError("Please enter time before proceed", requireContext())
                }

            }
        }

        t5DialogUi?.cancelButton?.setOnClickListener {
            t5DialogUi!!.dialog.dismiss()
        }
    }
}