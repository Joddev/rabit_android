package com.cyhee.android.rabit.sign.login

import com.cyhee.android.rabit.api.response.TokenData
import com.cyhee.android.rabit.base.BasePresenter
import com.cyhee.android.rabit.base.BaseView
import retrofit2.Call
import retrofit2.Response

interface LoginContract {
    interface View : BaseView<Presenter> {
        fun success(tokenData: TokenData)
        fun fail(t: Throwable?)
    }

    interface Presenter : BasePresenter {
        fun login(username: String, password: String)
    }
}