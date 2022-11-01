package ru.netology.nmedia.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.activity.EditPostFragment

class EditPostResultContract : ActivityResultContract <Post, String?>() {

    override fun createIntent(context: Context, input: Post): Intent {
       val intent  = Intent(context, EditPostFragment::class.java)
        intent.putExtra(Intent.EXTRA_TEXT, input.content)
        return intent
    }


    override fun parseResult(resultCode: Int, intent: Intent?): String? =
      if (resultCode == Activity.RESULT_OK) {
        intent?.getStringExtra(Intent.EXTRA_TEXT)
      } else {
          null
      }


}