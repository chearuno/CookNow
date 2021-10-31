package com.msc.app.cook.main_fragments

import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.kaopiz.kprogresshud.KProgressHUD
import com.msc.app.cook.ImagesActivity
import com.msc.app.cook.ImagesActivity.selectedImageList
import com.msc.app.cook.MainActivity
import com.msc.app.cook.R
import com.msc.app.cook.adaptor.SelectedImageAdapter
import com.msc.app.cook.models.ItemCategory
import com.msc.app.cook.new_recipe.FragmentNewIngredients
import com.msc.app.cook.utils.CustomDialog
import com.msc.app.cook.utils.Utils
import com.msc.app.cook.utils.Utils.alerterDialog
import com.msc.app.cook.utils.Utils.toastError
import kotlinx.android.synthetic.main.fragment_new_recipe.view.*
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.contentView
import java.text.SimpleDateFormat
import java.util.*


class FragmentNewRecipe : Fragment() {

    var selectedImageRecyclerView: RecyclerView? = null
    var selectedImageAdapter: SelectedImageAdapter? = null
    private var progressHUD: KProgressHUD? = null
    private var db: FirebaseFirestore? = null
    private var storage: FirebaseStorage? = null
    private var textCategoryName: TextView? = null

    override fun onCreate(a: Bundle?) {
        super.onCreate(a)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_new_recipe, container, false)
        (activity as MainActivity?)!!.setupToolbar(
            R.id.toolbar,
            "NEW RECIPE",
            R.color.colorPink,
            R.color.colorWhiteTrans,
            R.drawable.ic_burger
        )

        selectedImageRecyclerView = view.findViewById(R.id.selected_recycler_view)

        progressHUD = KProgressHUD.create(requireContext())
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("We fetching category list.")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)

        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        textCategoryName = view.findViewById(R.id.txtCat)

        view.btn_next.setOnClickListener {
            when {
                selectedImageList.isEmpty() -> {
                    alerterDialog("Error!", "Please select an images.", requireActivity())
                }
                view.recipe_name.text.isEmpty() -> {
                    alerterDialog("Error!", "Recipe name should not be empty.", requireActivity())
                }
                textCategoryName!!.text.toString() == "--" -> {
                    alerterDialog("Error!", "Please Select Category!", requireActivity())
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

                    var slsectedCatId = 100L
                    Utils.categoryItemList.forEach {

                        if (textCategoryName!!.text.toString() == it.categoryName) {
                            slsectedCatId = it.id!!
                        }
                    }

                    val putData: HashMap<String, Any> = HashMap()
                    putData["name"] = view.recipe_name.text.toString()
                    putData["likes"] = 0
                    putData["userId"] = userId.toInt()
                    putData["categoryId"] = slsectedCatId
                    putData["createdBy"] = firstName + lastName
                    putData["description"] = view.recipe_description.text.toString()
                    putData["isPrivate"] = view.checkBox.isChecked
                    putData["prepare_time"] = view.txt_cooling_t.text.toString()
                    putData["createdDate"] =
                        SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)

                    val tempFragment = FragmentNewIngredients()
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

        view.qtyLayoutSub.setOnClickListener {
            selectCategory()
        }

        return view
    }

    private fun selectCategory() {
        if (Utils.categoryItemList.isNotEmpty()) {
            showMeasurementDialog()
        } else {
            progressHUD!!.show()
            db = FirebaseFirestore.getInstance()

            db!!.collection("Category")
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {

                            Log.e("Doc", document.id)
                            val documentData = document.data
                            val title = documentData["name"]
                            val id = documentData["id"]
                            val image = documentData["image"]

                            val storageRef = storage!!.reference

                            val downloadRef = storageRef.child("CategoryImages/$image")

                            downloadRef.downloadUrl.addOnSuccessListener { uri ->
                                Log.e("Doc1", " => $uri")
                                val imageURI = uri.toString()
                                val item = ItemCategory()
                                item.categoryName = title as String?
                                item.id = id as Long?
                                item.img = imageURI
                                Utils.categoryItemList.add(item)
                                progressHUD!!.dismiss()
                            }
                                .addOnFailureListener {
                                    Log.e("Doc", "Error getting Data: ")
                                    progressHUD!!.dismiss()
                                }
                        }


                    } else {
                        Log.e("Doc", "Error getting documents: ", task.exception)
                        progressHUD!!.dismiss()
                    }
                }
        }
    }

    private fun showMeasurementDialog() {

        val wsList: Array<String?> = arrayOfNulls(Utils.categoryItemList.size)
        var i = 0
        Utils.categoryItemList.forEach {
            wsList[i] = it.categoryName.toString()
            i++
        }

        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("Select Measurement Unit")

        builder.setItems(wsList) { _, which ->
            val selected = wsList[which]
            textCategoryName!!.text = selected


        }

        val dialog = builder.create()
        dialog.show()

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