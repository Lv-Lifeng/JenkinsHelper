package com.lvlifeng.jenkinshelper.jenkins

import com.lvlifeng.jenkinshelper.bean.AccountStatus
import com.lvlifeng.jenkinshelper.jenkins.Credentials.Companion.getPassword
import com.offbytwo.jenkins.JenkinsServer
import com.offbytwo.jenkins.client.JenkinsHttpClient
import org.apache.commons.lang3.StringUtils
import java.net.URI
import java.net.URISyntaxException


/**
 *
 *
 * @author Lv Lifeng
 * @date 2022-05-08 10:27
 */
class Jenkins constructor() {

    var nickName: String? = null
        get() = field
        set(value) {
            field = value
        }

    var apiUrl: String? = null
        get() = field
        set(value) {
            field = value
        }
    var userName: String? = null
        get() = field
        set(value) {
            field = value
        }

    constructor(nickName: String?, apiUrl: String?, userName: String?) : this() {
        this.nickName = nickName
        this.apiUrl = apiUrl
        this.userName = userName
    }

    companion object {
        fun vaildAndSave(newJk: Jenkins, newJkPassword: String, jk: Jenkins?): Boolean {
            var jenkinsServer: JenkinsServer? = JenkinsServer(
                URI(newJk.apiUrl),
                newJk.userName,
                newJkPassword
            )

            jenkinsServer?.let {
                if (jenkinsServer.version != null
                    && StringUtils.isNotBlank(jenkinsServer.version.literalVersion)
                    &&  jenkinsServer.version.literalVersion != "-1") {
                    AccountState.addAccount(newJk, newJkPassword, jk)
                    return true
                }
            }
            return false
        }

        fun validAndGet(jk: Jenkins): AccountStatus {
            var jenkinsServer: JenkinsServer? = null
            try {
                jenkinsServer = JenkinsServer(
                    URI(jk.apiUrl),
                    jk.userName,
                    getPassword(jk.apiUrl + jk.userName)
                )
            } catch (e: URISyntaxException) {
                e.printStackTrace()
            }
            jenkinsServer?.let {
                if (jenkinsServer.version != null
                    && StringUtils.isNotBlank(jenkinsServer.version.literalVersion)
                    &&  jenkinsServer.version.literalVersion != "-1") {
                    return AccountStatus(true, it)
                }
            }
            return AccountStatus(false, null)
        }
    }

    fun client(jk: Jenkins): JenkinsHttpClient? {
        var jenkinsHttpClient: JenkinsHttpClient? = null
        try {
            jenkinsHttpClient = JenkinsHttpClient(URI(jk.apiUrl), jk.userName, jk.getCpassword())
        } catch (e: URISyntaxException) {
            e.printStackTrace()
        }
        return jenkinsHttpClient
    }

    fun getCpassword(): String? {
        return Credentials.getPassword(this.apiUrl + this.userName)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Jenkins

        if (apiUrl != other.apiUrl) return false
        if (userName != other.userName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = apiUrl?.hashCode() ?: 0
        result = 31 * result + (userName?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "$nickName"
    }


}