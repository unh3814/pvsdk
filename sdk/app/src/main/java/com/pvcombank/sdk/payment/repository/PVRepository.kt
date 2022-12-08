package com.pvcombank.sdk.payment.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.pvcombank.sdk.BuildConfig
import com.pvcombank.sdk.payment.model.CardModel
import com.pvcombank.sdk.payment.model.Constants
import com.pvcombank.sdk.payment.model.GetAccessTokenModel
import com.pvcombank.sdk.payment.model.MasterModel
import com.pvcombank.sdk.payment.model.request.RequestModel
import com.pvcombank.sdk.payment.model.request.RequestPurchase
import com.pvcombank.sdk.payment.model.request.RequestVerifyOTP
import com.pvcombank.sdk.payment.model.response.ResponsePurchase
import com.pvcombank.sdk.payment.model.response.ResponseVerifyOTP
import com.pvcombank.sdk.payment.network.ApiHelper
import com.pvcombank.sdk.payment.network.ApiOther
import com.pvcombank.sdk.payment.network.RetrofitHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class PVRepository : RepositoryBase() {
    private val apiHelper: ApiHelper = RetrofitHelper.instance()
        .createServices(Constants.BASE_URL)
        .create(ApiHelper::class.java)

    private val apiOther: ApiOther = RetrofitHelper.instance()
        .createServices(BuildConfig.SERVER_URL)
        .create(ApiOther::class.java)

    private val masterData = MasterModel.getInstance()

    val observableMethods = MutableLiveData<List<CardModel>>()
    val observableListCard = MutableLiveData<List<CardModel>>()
    val observableCard = MutableLiveData<CardModel>()
    val observableMethodsDetail = MutableLiveData<CardModel>()
    val observablePurchase = MutableLiveData<ResponsePurchase>()
    val observableVerify = MutableLiveData<ResponseVerifyOTP>()

    fun getTokenByCode(
        code: String,
        clientId: String,
        clientSecret: String,
        callBack: (GetAccessTokenModel?) -> Unit
    ) {
        apiHelper.apply {
            this.getAccessToken(code = code, clientId = clientId, clientSecret = clientSecret)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    {
                        callBack.invoke(it)
                    },
                    {
                        callBack.invoke(null)
                        Log.e("ERROR", it.message.toString())
                    }
                )
        }
    }

    fun getListCard() {
        apiOther.apply {
            this.getListCardDetail()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { handlerSuccess(it, observableListCard) },
                    { handlerError(it) }
                )
        }
    }

    fun getMethods() {
        apiOther.getMethods()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { handlerSuccess(it, observableMethods) },
                { handlerError(it) }
            )
    }

    fun getCard(cardToken: String) {
        apiOther.getCardDetail(cardToken)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { handlerSuccess(it, observableCard) },
                { handlerError(it) }
            )
    }

    fun getMethodsDetail(type: String, source: String) {
        apiOther.getMethodsDetail(type, source)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    handlerSuccess(
                        it,
//					Constants.API_METHODS_DETAIL
                        observableMethodsDetail
                    )
                },
                { handlerError(it) }
            )
    }

    fun purchase(cardModel: CardModel) {
        apiOther.apply {
            val request = RequestModel(
                data = RequestPurchase(
                    amount = masterData.orderCurrency?.toLong() ?: 0L,
                    description = masterData.orderDesc,
                    traceNumber = masterData.idOrder,
                    cardToken = cardModel.cardToken,
                    type = cardModel.type,
                    source = cardModel.source
                )
            ).request()
            purchase(request = request)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { handlerSuccess(it, observablePurchase) },
                    { handlerError(it) }
                )
        }
    }

    fun verifyOTP(data: RequestVerifyOTP) {
        val request = RequestModel(
            data = data
        ).request()
        apiOther.verifyOTP(request)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { handlerSuccess(it, observableVerify) },
                { handlerError(it) }
            )
    }

	override fun clear() {
		observableMethods.postValue(null)
		observableListCard.postValue(null)
		observableCard.postValue(null)
		observableMethodsDetail.postValue(null)
		observablePurchase.postValue(null)
	}
}