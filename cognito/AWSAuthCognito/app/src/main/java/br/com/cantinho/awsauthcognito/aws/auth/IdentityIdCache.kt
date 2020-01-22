package br.com.cantinho.awsauthcognito.aws.auth

interface IdentityIdCache {
    fun read(username: String): String
    fun store(username: String, identityId: String)
}