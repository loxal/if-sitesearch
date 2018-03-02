/*
 * Copyright 2017 IntraFind Software AG. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intrafind.sitesearch.client

import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear

fun main(args: Array<String>) {
    window.addEventListener("DOMContentLoaded", {
        init()
    })
}

fun triggerFirstUsageOwnership() {
    val serviceUrl: String
    if (window.location.hostname.equals("localhost")) {
        serviceUrl = "http://localhost:8001"
    } else {
        serviceUrl = "https://api.sitesearch.cloud"
    }

    val xhr = XMLHttpRequest()
    val feedUrl = (document.getElementById("feedUrl") as HTMLInputElement).value
    xhr.open("POST", "$serviceUrl/sites/rss?feedUrl=$feedUrl&stripHtmlTags=true")
    xhr.onload = {
        val siteId = JSON.parse<dynamic>(xhr.responseText).siteId as String
        val siteSecret = JSON.parse<dynamic>(xhr.responseText).siteSecret as String
        (document.getElementById("siteId") as HTMLInputElement).value = siteId
        (document.getElementById("siteSecret") as HTMLInputElement).value = siteSecret
        document.cookie = "override-site = $siteId"
        document.dispatchEvent(Event("triggerFirstUsageOwnershipEvent"))
    }
    xhr.send()
}

private val loginLink = document.getElementById("loginLink") as HTMLAnchorElement
fun init() {
    loginLink.text = " Login"
    if (window.location.hostname.equals("localhost")) {
        loginLink.href = "http://${window.location.host}/login?redirect_uri=http://${window.location.host}/login"
    } else {
        loginLink.href = "https://api.sitesearch.cloud/login?redirect_uri=https://api.sitesearch.cloud/login"
    }

    val xhr = XMLHttpRequest()
    xhr.open("GET", "/user")
    xhr.onload = {
        showUser(xhr)
    }
    xhr.send()
}

fun showInitCode() {
    val siteIdContainer = document.getElementById("siteId") as HTMLInputElement
    val enterpriseSearchbar = document.getElementById("sitesearch-searchbar") as HTMLDivElement
    val finderInit = document.getElementById("sitesearch-page-finder-init") as HTMLScriptElement
    val finderContainer = document.getElementById("page-finder") as HTMLDivElement
    val finderVariant = document.getElementById("finder-variant") as HTMLInputElement
//    val searchbarInputField = document.getElementById("ifs-sb-searchfield") as HTMLInputElement
    val searchbarVariant = document.getElementById("searchbar-variant") as HTMLInputElement
    val integrationCode = document.getElementById("integration-code") as HTMLTextAreaElement
    window.addEventListener("DOMContentLoaded", { console.warn(1) })
    document.addEventListener("DOMContentLoaded", { console.warn(2) })
//    searchbarInputField.placeholder = "Search for \"knowledge\""
    val enterpriseSearchbarCode = enterpriseSearchbar.outerHTML
            .replace("searchbar-config/sitesearch-config.json", "https://cdn.sitesearch.cloud/searchbar/latest/config/sitesearch.json")
    integrationCode.value = enterpriseSearchbarCode
    finderContainer.style.display = "none"
    val finderInitCode = "<script src=\"https://api.sitesearch.cloud/app/runtime/kotlin.js\"></script>\n" +
            finderInit.outerHTML
                    .replace("/app/finder/finder.js", "https://api.sitesearch.cloud/app/finder/finder.js")

    searchbarVariant.addEventListener("click", {
        enterpriseSearchbar.style.display = "block"
        finderContainer.style.display = "none"
        if (siteIdContainer.value.isBlank()) {
            integrationCode.value = enterpriseSearchbarCode
        } else {
            integrationCode.value = enterpriseSearchbarCode.replace("siteId\\:\\ \".+".toRegex(), "siteId: \"${siteIdContainer.value}\"")
        }
    })

    finderVariant.addEventListener("click", {
        enterpriseSearchbar.style.display = "none"
        finderContainer.style.display = "block"
        if (siteIdContainer.value.isBlank()) {
            integrationCode.value = finderInitCode
        } else {
            integrationCode.value = finderInitCode.replace("data-siteId=\".+\"".toRegex(RegexOption.IGNORE_CASE), "data-siteId=\"${siteIdContainer.value}\"")
        }
    })

    document.addEventListener("triggerFirstUsageOwnershipEvent", {
        if (!siteIdContainer.value.isBlank()) {
            if (searchbarVariant.checked) {
                integrationCode.value = integrationCode.value.replace("siteId\\:\\ \".+".toRegex(), "siteId: \"${siteIdContainer.value}\"")
            } else {
                integrationCode.value = integrationCode.value.replace("data-siteId=\".+\"".toRegex(RegexOption.IGNORE_CASE), "data-siteId=\"${siteIdContainer.value}\"")
            }
        }
    })
}

private fun showUser(xhr: XMLHttpRequest) {
    if (xhr.status.equals(200) && xhr.responseText.isEmpty()) {
        loginLink.text = " Login"
    } else
        if (xhr.status.equals(200) && xhr.response != null) {
            showAssignments()
            val user = JSON.parse<dynamic>(xhr.responseText)
            (document.getElementById("assignmentController") as HTMLDivElement).style.display = "block"
            loginLink.textContent = " Logout: ${user.userAuthentication.details.name}"
            loginLink.title = user.userAuthentication.details.id
            loginLink.className = "fa fa-sign-out"
            loginLink.href = "/logout"
            if (document.getElementById("company") != null) {
                (document.getElementById("company") as HTMLInputElement).value = user.userAuthentication.details.company
                (document.getElementById("contactEmail") as HTMLInputElement).value = user.userAuthentication.details.email
            }
        }
}

@JsName("assignSite")
private fun assignSite() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "/user")
    xhr.onload = {
        if (xhr.status.equals(200) && xhr.response != null) {
            val user = JSON.parse<dynamic>(xhr.responseText)
            val assignmentsUpdate = XMLHttpRequest()
            assignmentsUpdate.open("PUT", "/assignments/sites/"
                    + (document.getElementById("siteId") as HTMLInputElement).value + "?siteSecret="
                    + (document.getElementById("siteSecret") as HTMLInputElement).value
                    + "&siteName=" + (document.getElementById("siteName") as HTMLInputElement).value)
            assignmentsUpdate.setRequestHeader("content-type", "application/json")
            assignmentsUpdate.onload = {
                showAssignments()
            }

            val tenantSiteAssignment: dynamic = Any()
            tenantSiteAssignment.authProviderId = user.userAuthentication.details.id
            tenantSiteAssignment.authProvider = "github"
            tenantSiteAssignment.authProviderToken = user.details.tokenValue
            tenantSiteAssignment.contactEmail = (document.getElementById("contactEmail") as HTMLInputElement).value
            tenantSiteAssignment.company = (document.getElementById("company") as HTMLInputElement).value

//            assignmentsUpdate.send(JSON.stringify("""
//                "authProviderId": "${user.userAuthentication.details.id}",
//                "authProvider": "github",
//                "authProviderToken": "${user.details.tokenValue}",
//                "contactEmail": "${(document.getElementById("contactEmail") as HTMLInputElement).value}",
//                "company": "${(document.getElementById("company") as HTMLInputElement).value}"
//            """))
            assignmentsUpdate.send(JSON.stringify(tenantSiteAssignment))
        }
    }
    xhr.send()
}

private fun showAssignments() {
    val xhr = XMLHttpRequest()
    xhr.open("GET", "/user")
    xhr.onload = {
        if (xhr.status.equals(200) && xhr.response != null) {
            val user = JSON.parse<dynamic>(xhr.responseText)
            val authProviderRetrieval = XMLHttpRequest()
            authProviderRetrieval.open("GET", "/authentication-providers/github/" + user.userAuthentication.details.id
                    + "?accessToken=" + user.details.tokenValue)
            authProviderRetrieval.setRequestHeader("content-type", "application/json")
            authProviderRetrieval.onload = {
                if (authProviderRetrieval.status.equals(200) && authProviderRetrieval.response != null) {
                    document.getElementById("assignments")?.clear()
                    document.getElementById("assignmentsContainer")?.setAttribute("style", "display: block;")
                    val assignments = JSON.parse<dynamic>(authProviderRetrieval.responseText)
                    assignments.tenants.forEach({ tenant ->
                        val tenantCompany = document.getElementById("companyName") as HTMLElement
                        val tenantContactEmail = document.getElementById("companyContact") as HTMLElement
                        tenantCompany.textContent = "Company: " + tenant.company
                        tenantContactEmail.textContent = "Contact: " + tenant.contactEmail

                        tenant.sites.forEach({ site ->
                            val entry = document.createElement("tr") as HTMLTableRowElement
                            val siteId = document.createElement("td") as HTMLTableCellElement
                            siteId.textContent = site.id
                            val siteSecret = document.createElement("td") as HTMLTableCellElement
                            siteSecret.textContent = site.secret
                            val siteName = document.createElement("td") as HTMLTableCellElement
                            siteName.textContent = site.name
                            entry.appendChild(siteName)
                            entry.appendChild(siteId)
                            entry.appendChild(siteSecret)
                            document.getElementById("assignments")?.appendChild(entry)
                        })
                    })
                }
            }
            authProviderRetrieval.send()
        }
    }
    xhr.send()
}

