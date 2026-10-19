package com.example.data.remote

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Menunggu hasil dari Task milik Play Services/Firebase secara coroutine.
 */
suspend fun <T> Task<T>.awaitResult(): T = suspendCancellableCoroutine { cont ->
  addOnCompleteListener { task ->
    if (task.isSuccessful) {
      cont.resume(task.result)
    } else {
      cont.resumeWithException(task.exception ?: RuntimeException("Task gagal tanpa keterangan"))
    }
  }
  addOnCanceledListener { cont.cancel() }
}
