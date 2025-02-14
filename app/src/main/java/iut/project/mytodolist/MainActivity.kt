package iut.project.mytodolist

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.DialogInterface
import android.content.Intent
import android.text.Editable
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import iut.project.mytodolist.adapter.MyListAdapter
import iut.project.mytodolist.classes.TaskModelClass
import iut.project.mytodolist.handler.DatabaseHandler
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Locale.setDefault(Locale.ENGLISH)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.todo -> {
                    addIncludeLayoutContent(findViewById<View>(R.id.content_main))
                    true
                }
                R.id.late -> {
                    addIncludeLayoutContent(findViewById<View>(R.id.content_late))
                    true
                }
                R.id.done -> {
                    addIncludeLayoutContent(findViewById<View>(R.id.content_done))
                    true
                }
                R.id.settings -> {
                    addIncludeLayoutContent(findViewById<View>(R.id.content_settings))
                    true
                }
                else -> false
            }
        }
        // Set the default view
        addIncludeLayoutContent(findViewById<View>(R.id.content_main))
    }

    private fun addIncludeLayoutContent(layoutId: View) {
        // Remove all views from the main layout
        findViewById<View>(R.id.content_main).visibility = View.GONE
        findViewById<View>(R.id.content_late).visibility = View.GONE
        findViewById<View>(R.id.content_done).visibility = View.GONE
        findViewById<View>(R.id.content_settings).visibility = View.GONE
        // Make the selected layout visible
        layoutId.visibility = View.VISIBLE
        viewRecord()
    }

    override fun onStart() {
        super.onStart()
        viewRecord()
    }

    fun getDate(offset: Int): String {
        val currentDate = Calendar.getInstance()
        currentDate.add(Calendar.DAY_OF_MONTH, offset)
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(currentDate.time)
    }
    val tomorrowDate = getDate(1)
    val todayDate = getDate(0)
    val yesterdayDate = getDate(-1)

    private val EMPTY_TEXT = "Name or description cannot be empty"

    // Method for reading records from database in ListView
    fun viewRecord() {
        // Creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val task: List<TaskModelClass> = databaseHandler.viewTask()

        val main = findViewById<View>(R.id.content_main).visibility
        val late = findViewById<View>(R.id.content_late).visibility
        val done = findViewById<View>(R.id.content_done).visibility

        var currentDate = Date()
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        val dateString = sdf.format(currentDate)
        val maxDate = sdf.parse("31/12/9999")

        val taskArrayId = mutableListOf<String>()
        val taskArrayName = mutableListOf<String>()
        val taskArrayDescription = mutableListOf<String>()
        val taskArrayDate = mutableListOf<String>()
        val taskArrayCheckbox = mutableListOf<Int>()

        if (main == View.VISIBLE) {
            for (tas in task) {
                var taskDate = Date()
                if (tas.taskDate.isNotEmpty()) {
                    taskDate = sdf.parse(tas.taskDate)
                }
                if ((tas.taskDate.isEmpty() || taskDate.after(currentDate) || sdf.format(taskDate) == dateString) && tas.taskDone != 1) {
                    taskArrayId.add(tas.taskId.toString())
                    taskArrayName.add(tas.taskName)
                    taskArrayDescription.add(tas.taskDescription)
                    taskArrayDate.add(tas.taskDate)
                    taskArrayCheckbox.add(tas.taskDone)
                }
            }
            // Sort tasks by date
            taskArrayId.indices.forEach { i ->
                for (j in i + 1 until taskArrayId.size) {
                    val dateI = if (taskArrayDate[i].isNotEmpty()) sdf.parse(taskArrayDate[i]) else maxDate
                    val dateJ = if (taskArrayDate[j].isNotEmpty()) sdf.parse(taskArrayDate[j]) else maxDate
                    if (dateI.after(dateJ)) {
                        // Swap elements
                        Collections.swap(taskArrayId, i, j)
                        Collections.swap(taskArrayName, i, j)
                        Collections.swap(taskArrayDescription, i, j)
                        Collections.swap(taskArrayDate, i, j)
                        Collections.swap(taskArrayCheckbox, i, j)
                    }
                }
            }
            // Creating custom ArrayAdapter
            val myListAdapter = MyListAdapter(
                this,
                taskArrayId.toTypedArray(),
                taskArrayName.toTypedArray(),
                taskArrayDescription.toTypedArray(),
                taskArrayDate.toTypedArray(),
                taskArrayCheckbox.toTypedArray(),
                this
            )
            findViewById<ListView>(R.id.listView).adapter = myListAdapter
        }
        if (late == View.VISIBLE) {
            for (tas in task) {
                var taskDate = Date()
                if (tas.taskDate.isNotEmpty()) {
                    taskDate = sdf.parse(tas.taskDate)
                }
                if (taskDate.before(currentDate) && sdf.format(taskDate) != dateString && tas.taskDone != 1) {
                    taskArrayId.add(tas.taskId.toString())
                    taskArrayName.add(tas.taskName)
                    taskArrayDescription.add(tas.taskDescription)
                    taskArrayDate.add(tas.taskDate)
                    taskArrayCheckbox.add(tas.taskDone)
                }
            }
            // Sort tasks by date
            taskArrayId.indices.forEach { i ->
                for (j in i + 1 until taskArrayId.size) {
                    val dateI = if (taskArrayDate[i].isNotEmpty()) sdf.parse(taskArrayDate[i]) else maxDate
                    val dateJ = if (taskArrayDate[j].isNotEmpty()) sdf.parse(taskArrayDate[j]) else maxDate
                    if (dateI.after(dateJ)) {
                        // Swap elements
                        Collections.swap(taskArrayId, i, j)
                        Collections.swap(taskArrayName, i, j)
                        Collections.swap(taskArrayDescription, i, j)
                        Collections.swap(taskArrayDate, i, j)
                        Collections.swap(taskArrayCheckbox, i, j)
                    }
                }
            }
            // Creating custom ArrayAdapter
            val myListAdapter = MyListAdapter(
                this,
                taskArrayId.toTypedArray(),
                taskArrayName.toTypedArray(),
                taskArrayDescription.toTypedArray(),
                taskArrayDate.toTypedArray(),
                taskArrayCheckbox.toTypedArray(),
                this
            )
            findViewById<ListView>(R.id.listLateView).adapter = myListAdapter
        }
        if (done == View.VISIBLE) {
            for (tas in task) {
                if (tas.taskDone == 1) {
                    taskArrayId.add(tas.taskId.toString())
                    taskArrayName.add(tas.taskName)
                    taskArrayDescription.add(tas.taskDescription)
                    taskArrayDate.add(tas.taskDate)
                    taskArrayCheckbox.add(tas.taskDone)
                }
            }
            // Sort tasks by date
            taskArrayId.indices.forEach { i ->
                for (j in i + 1 until taskArrayId.size) {
                    val dateI = if (taskArrayDate[i].isNotEmpty()) sdf.parse(taskArrayDate[i]) else maxDate
                    val dateJ = if (taskArrayDate[j].isNotEmpty()) sdf.parse(taskArrayDate[j]) else maxDate
                    if (dateI.after(dateJ)) {
                        // Swap elements
                        Collections.swap(taskArrayId, i, j)
                        Collections.swap(taskArrayName, i, j)
                        Collections.swap(taskArrayDescription, i, j)
                        Collections.swap(taskArrayDate, i, j)
                        Collections.swap(taskArrayCheckbox, i, j)
                    }
                }
            }
            // Creating custom ArrayAdapter
            val myListAdapter = MyListAdapter(
                this,
                taskArrayId.toTypedArray(),
                taskArrayName.toTypedArray(),
                taskArrayDescription.toTypedArray(),
                taskArrayDate.toTypedArray(),
                taskArrayCheckbox.toTypedArray(),
                this
            )
            findViewById<ListView>(R.id.listDoneView).adapter = myListAdapter
        }
    }

    // Method for updating records based on user id
    fun updateRecord(view: View) {
        val dialogBuilder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.update_dialog, null)
        dialogBuilder.setView(dialogView)

        val edtName = dialogView.findViewById(R.id.updateName) as EditText
        val edtDescription = dialogView.findViewById(R.id.updateDescription) as EditText
        val edtDate = dialogView.findViewById(R.id.updateDate) as DatePicker

        val id = view.contentDescription.toString().split(",")[0]

        val name = view.contentDescription.toString().split(",")[1]
        edtName.text = Editable.Factory.getInstance().newEditable(name.substring(1))

        val description = view.contentDescription.toString().split(",")[2]
        edtDescription.text = Editable.Factory.getInstance().newEditable(description.substring(1))

        val date = view.contentDescription.toString().split(",")[3].substring(1, view.contentDescription.toString().split(",")[3].length)
        if (date == "") {
            edtDate.visibility = View.GONE
        } else {
            edtDate.visibility = View.VISIBLE
            val day = date.split("/")[0].toInt()
            val month = date.split("/")[1].toInt() - 1
            val year = date.split("/")[2].toInt()

            edtDate.init(year, month, day, null)
        }

        dialogBuilder.setTitle("Edit the task")
        dialogBuilder.setMessage("Edit the desired fields")
        dialogBuilder.setPositiveButton("Update", DialogInterface.OnClickListener { _, _ ->

            val updateName = edtName.text.toString()
            val updateDescription = edtDescription.text.toString()
            var updateDate: String
            if (edtDate.visibility == View.VISIBLE) {
                val updateDay = edtDate.dayOfMonth
                val updateMonth = edtDate.month + 1
                val updateYear = edtDate.year

                updateDate = if (updateMonth < 10) {
                    "$updateDay/0$updateMonth/$updateYear"
                }  else {
                    "$updateDay/$updateMonth/$updateYear"
                }
                updateDate = if (updateDay < 10) {
                    "0$updateDate"
                }  else {
                    "$updateDate"
                }
            } else {
                updateDate = ""
            }

            // Creating the instance of DatabaseHandler class
            val databaseHandler: DatabaseHandler = DatabaseHandler(this)
            if (updateName.trim() != "" && updateDescription.trim() != "") {
                // Calling the updateEmployee method of DatabaseHandler class to update record
                val status = databaseHandler.updateTask(
                    TaskModelClass(
                        Integer.parseInt(id.substring(1, id.length)),
                        updateName,
                        updateDescription,
                        updateDate,
                        0
                    )
                )
                if (status > -1) {
                    Toast.makeText(applicationContext, "Edit successful", Toast.LENGTH_LONG)
                        .show()
                    viewRecord()
                }
            } else {
                Toast.makeText(applicationContext, EMPTY_TEXT, Toast.LENGTH_LONG).show()
            }

        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { _, _ ->
            // Pass
        })
        val b = dialogBuilder.create()
        b.show()
    }

    // Method for deleting records based on id
    fun deleteRecord(view: View) {
        val id = view.contentDescription.toString()
        // Creating the instance of DatabaseHandler class
        val databaseHandler: DatabaseHandler = DatabaseHandler(this)
        val done = findViewById<View>(R.id.content_done).visibility == View.VISIBLE
        if (!done) {
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Confirmation")
            alertDialogBuilder.setMessage("Are you sure you want to delete this task?")
            alertDialogBuilder.setPositiveButton("DELETE") { dialog, which ->
                val status = databaseHandler.deleteTask(
                    TaskModelClass(
                        Integer.parseInt(id),
                        "",
                        "",
                        "",
                        0
                    )
                )
                if (status > -1) {
                    Toast.makeText(applicationContext, "Deletion successful", Toast.LENGTH_LONG)
                        .show()
                    viewRecord()
                }
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, which -> }
            val alertDialog: AlertDialog = alertDialogBuilder.create()
            alertDialog.show()
        } else {
            val status = databaseHandler.deleteTask(
                TaskModelClass(
                    Integer.parseInt(id),
                    "",
                    "",
                    "",
                    0
                )
            )
            if (status > -1) {
                Toast.makeText(applicationContext, "Deletion successful", Toast.LENGTH_LONG)
                    .show()
                viewRecord()
            }
        }

    }

    fun addTaskPage(view: View) {
        val intent = Intent(this, AddTaskActivity::class.java)
        startActivity(intent)
    }

    fun formatDate(s: String): CharSequence? {
        val parts = s.split("/")
        if (parts.size != 3) return null // The string does not match the format "dd/mm/yyyy"

        val day = parts[0].toIntOrNull() ?: return null // Conversion of the day to an integer, null if the conversion fails
        val month = parts[1].toIntOrNull()?.minus(1) ?: return null // Conversion of the month to an integer and subtraction of 1 to match the indices from 0 to 11, null if the conversion fails
        val year = parts[2].toIntOrNull() ?: return null // Conversion of the year to an integer, null if the conversion fails

        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )

        val monthName = monthNames.getOrNull(month) ?: return null // Retrieval of the month name, null if the index does not correspond to a month

        return String.format("%2d %s", day, monthName)
    }

}
