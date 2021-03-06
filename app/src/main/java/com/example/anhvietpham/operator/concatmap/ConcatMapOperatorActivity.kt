package com.example.anhvietpham.operator.concatmap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class ConcatMapOperatorActivity : AppCompatActivity() {
    private val TAG = ConcatMapOperatorActivity::class.java.simpleName
    private var disposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        concatCombine()
        getUsersObservable()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .concatMap { user ->
                getAddressObservable(user = user)
            }
            .subscribe(object : Observer<User>{
                override fun onComplete() {
                    Log.e("ConcatMapOperator", "All users emitted!")
                }

                override fun onSubscribe(d: Disposable) {
                    Log.e("ConcatMapOperator", "onSubscribe")
                    disposable = d
                }

                override fun onNext(t: User) {
                    Log.e("ConcatMapOperator", "onNext: " + t.name + ", " + t.gender + ", " + t.address?.city)
                }

                override fun onError(e: Throwable) {
                }
            })
    }

    private fun getAddressObservable(user: User): Observable<User> {

        val addresses = arrayOf(
            "1600 Amphitheatre Parkway, Mountain View, CA 94043",
            "2300 Traverwood Dr. Ann Arbor, MI 48105",
            "500 W 2nd St Suite 2900 Austin, TX 78701",
            "355 Main Street Cambridge, MA 02142"
        )

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                val address = Address()

                address.city = addresses[Random().nextInt(2) + 0]
                if (!emitter.isDisposed) {
                    user.address = address

                    // Generate network latency of random duration
                    val sleepTime = Random().nextInt(1000) + 500

                    Thread.sleep(sleepTime.toLong())
                    emitter.onNext(user)
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }

    private fun getUsersObservable(): Observable<User> {
        val maleUsers = arrayOf("Mark", "John", "Trump", "Obama")

        val users = mutableListOf<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                for (user in users) {
                    if (!emitter.isDisposed) {
                        emitter.onNext(user)
                    }
                }

                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }

    private fun concatCombine(){
        Observable
            .concat(getFemaleObservable(),getMaleObservable())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<User>{
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onNext(t: User) {
                    Log.e("ConcatCombine", t.name + ", " + t.gender)
                }

                override fun onError(e: Throwable) {
                }

            })
    }


    private fun getMaleObservable(): Observable<User> {
        val maleUsers = arrayOf("Mark", "John", "Trump","Obama")

        val users = mutableListOf<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "male"

            users.add(user)
        }

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                for (user in users) {
                    if (!emitter.isDisposed) {
                        Thread.sleep(1000)
                        emitter.onNext(user)
                    }
                }

                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }

    private fun getFemaleObservable(): Observable<User> {
        val maleUsers = arrayOf("Lucy", "Scarlett", "April")

        val users = mutableListOf<User>()

        for (name in maleUsers) {
            val user = User()
            user.name = name
            user.gender = "Famle"

            users.add(user)
        }

        return Observable
            .create(ObservableOnSubscribe<User> { emitter ->
                for (user in users) {
                    if (!emitter.isDisposed) {
                        Thread.sleep(3000)
                        emitter.onNext(user)
                    }
                }

                if (!emitter.isDisposed) {
                    emitter.onComplete()
                }
            }).subscribeOn(Schedulers.io())
    }


    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }

    class User(
        var name: String = "",
        var email: String = "",
        var gender: String = "",
        var address: Address? = null
    )
    class Address(var city: String = "")
}