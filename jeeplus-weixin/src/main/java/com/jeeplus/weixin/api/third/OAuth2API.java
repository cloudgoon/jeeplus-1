﻿package com.jeeplus.weixin.api.third;

import java.util.HashMap;
import java.util.Map;

import com.jeeplus.weixin.api.core.common.WeixinHttpUtils;
import net.sf.json.JSONObject;

import com.jeeplus.weixin.api.core.exception.WexinReqException;
import com.jeeplus.weixin.api.third.model.ApiAuthorizerToken;
import com.jeeplus.weixin.api.third.model.ApiAuthorizerTokenRet;
import com.jeeplus.weixin.api.third.model.ApiComponentToken;
import com.jeeplus.weixin.api.third.model.ApiGetAuthorizer;
import com.jeeplus.weixin.api.third.model.ApiGetAuthorizerRet;
import com.jeeplus.weixin.api.third.model.GetPreAuthCodeParam;
import com.jeeplus.weixin.api.third.model.ReOpenAccessToken;
import com.jeeplus.weixin.api.wxstore.order.model.OrderInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 对应微信API的  "用户管理"=> "网页授权获取用户基本信息”
 * Created by yuzp17311 on 2016/8/23.
 * 
 */
public class OAuth2API {
	private static Logger logger = LoggerFactory.getLogger(OAuth2API.class);
	//获取预授权码
	private static String api_create_preauthcode_url = "https://api.weixin.qq.com/cgi-bin/component/api_create_preauthcode?component_access_token=COMPONENT_ACCESS_TOKEN";
	private static String api_component_token_url = "https://api.weixin.qq.com/cgi-bin/component/api_component_token";
	private static String get_access_token_bycode_url = "https://api.weixin.qq.com/sns/oauth2/component/access_token?appid=APPID&code=CODE&grant_type=authorization_code&component_appid=COMPONENT_APPID&component_access_token=COMPONENT_ACCESS_TOKEN";
	private static String api_query_auth_url = "https://api.weixin.qq.com/cgi-bin/component/api_query_auth?component_access_token=xxxx";
	//客服接口地址
    public static String send_message_url = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=ACCESS_TOKEN";
    //4、获取（刷新）授权公众号的令牌
    private static String api_authorizer_token_url = "https:// api.weixin.qq.com /cgi-bin/component/api_authorizer_token?component_access_token=COMPONENT_ACCESS_TOKEN";
    //5、获取授权方的账户信息
    private static String api_get_authorizer_info_url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_info?component_access_token=COMPONENT_ACCESS_TOKEN";
    //6、获取授权方的选项设置信息
    private static String api_get_authorizer_option_url = "https://api.weixin.qq.com/cgi-bin/component/api_get_authorizer_option?component_access_token=COMPONENT_ACCESS_TOKEN";
    //7、设置授权方的选项信息
    private static String api_set_authorizer_option_url = "https://api.weixin.qq.com/cgi-bin/component/api_set_authorizer_option?component_access_token=COMPONENT_ACCESS_TOKEN";
    /**
	 * 1、获取第三方平台access_token
	 * @param apiComponentToken
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public static String getAccessToken(ApiComponentToken apiComponentToken) throws WexinReqException{
		String component_access_token = "";
		String requestUrl = api_component_token_url;
		JSONObject obj = JSONObject.fromObject(apiComponentToken);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl, "POST", obj.toString());
		if (result.has("errcode")) {
			logger.error("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		} else {
			component_access_token = result.getString("component_access_token");
		}
		return component_access_token;
	}
	
	/**
	 * 2、获取预授权码
	 * @param component_appid
	 * @param component_access_token
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public static String getPreAuthCode(String component_appid, String component_access_token) throws WexinReqException{
		String pre_auth_code = "";
		String requestUrl = api_create_preauthcode_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
		GetPreAuthCodeParam getPreAuthCodeParam = new GetPreAuthCodeParam();
		getPreAuthCodeParam.setComponent_appid(component_appid);
		JSONObject obj = JSONObject.fromObject(getPreAuthCodeParam);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl, "POST", obj.toString());
		if (result.has("errcode")) {
			logger.error("获取权限令牌信息！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取权限令牌信息！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		} else {
			pre_auth_code = result.getString("pre_auth_code");
		}
		return pre_auth_code;
	}
	
	/**
	 * 3、使用授权码换取公众号的授权信息
	 * @param component_appid
	 * @param authorization_code
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public static JSONObject getApiQueryAuthInfo(String component_appid,String authorization_code,String component_access_token) throws WexinReqException{
		String requestUrl = api_query_auth_url.replace("xxxx", component_access_token);
		Map<String,String> mp = new HashMap<String,String>();
		mp.put("component_appid", component_appid);
		mp.put("authorization_code", authorization_code);
		JSONObject obj = JSONObject.fromObject(mp);
		System.out.println("-------------------3、使用授权码换取公众号的授权信息---requestUrl------------------------"+requestUrl);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"POST", obj.toString());
		if (result.has("errcode")) {
			logger.error("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		}
		return result;
	}
	
	
	/**
	 * 4、获取（刷新）授权公众号的令牌
	 * @param apiAuthorizerToken
	 * @param component_access_token
	 */
	public static ApiAuthorizerTokenRet apiAuthorizerToken(ApiAuthorizerToken apiAuthorizerToken,String component_access_token) throws WexinReqException{
		String requestUrl = api_authorizer_token_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
		JSONObject param = JSONObject.fromObject(apiAuthorizerToken);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"POST", param.toString());
		ApiAuthorizerTokenRet apiAuthorizerTokenRet = (ApiAuthorizerTokenRet)JSONObject.toBean(result, ApiAuthorizerTokenRet.class);
		return apiAuthorizerTokenRet;
	}
	/**
	 * 5、获取授权方的账户信息
	 */
	public static ApiGetAuthorizerRet apiGetAuthorizerInfo(ApiGetAuthorizer apiGetAuthorizer,String component_access_token) throws WexinReqException{
		String requestUrl = api_get_authorizer_info_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
		JSONObject param = JSONObject.fromObject(apiGetAuthorizer);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"POST", param.toString());
		ApiGetAuthorizerRet apiGetAuthorizerRet = (ApiGetAuthorizerRet)JSONObject.toBean(result, ApiGetAuthorizerRet.class);
		return apiGetAuthorizerRet;
	}
	
	/**
	 * 6、获取授权方的选项设置信息
	 */
	public static AuthorizerOptionRet apiGetAuthorizerOption(AuthorizerOption authorizerOption,String component_access_token) throws WexinReqException{
		String requestUrl = api_get_authorizer_option_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
		JSONObject param = JSONObject.fromObject(authorizerOption);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"POST", param.toString());
		AuthorizerOptionRet authorizerOptionRet = (AuthorizerOptionRet)JSONObject.toBean(result, AuthorizerOptionRet.class);
		return authorizerOptionRet;
	}
	/**
	 * 7、设置授权方的选项信息
	 */
	public static AuthorizerSetOptionRet apiSetAuthorizerOption(AuthorizerSetOption authorizerSetOption,String component_access_token) throws WexinReqException{
		String requestUrl = api_set_authorizer_option_url.replace("COMPONENT_ACCESS_TOKEN", component_access_token);
		JSONObject param = JSONObject.fromObject(authorizerSetOption);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"POST", param.toString());
		AuthorizerSetOptionRet authorizerSetOptionRet = (AuthorizerSetOptionRet)JSONObject.toBean(result, AuthorizerSetOptionRet.class);
		return authorizerSetOptionRet;
	}
	/**
	 * 微信主动推送给第三方服务器
	 * 8、推送component_verify_ticket协议
	 * 9、推送取消授权通知
	 */
	
	
	
	/**
	 * 获取第三方平台access_token
	 * @param appid
	 * @return kY9Y9rfdcr8AEtYZ9gPaRUjIAuJBvXO5ZOnbv2PYFxox__uSUQcqOnaGYN1xc4N1rI7NDCaPm_0ysFYjRVnPwCJHE7v7uF_l1hI6qi6QBsA
	 * @throws WexinReqException
	 */
	public static ReOpenAccessToken getAccessTokenByCode(String appid,String code,String grant_type,String component_appid,String component_access_token) throws WexinReqException{
		String requestUrl = get_access_token_bycode_url.replace("COMPONENT_APPID", component_appid).replace("COMPONENT_ACCESS_TOKEN", component_access_token).replace("authorization_code", grant_type).replace("CODE", code).replace("APPID", appid);
		JSONObject result = WeixinHttpUtils.httpRequest(requestUrl,"GET", null);
		ReOpenAccessToken reOpenAccessToken = (ReOpenAccessToken)JSONObject.toBean(result, OrderInfo.class);
		if (result.has("errcode")) {
			logger.error("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
			throw new WexinReqException("获取第三方平台access_token！errcode=" + result.getString("errcode") + ",errmsg = " + result.getString("errmsg"));
		}
		return reOpenAccessToken;
	}
	
	
	 /**
     * 发送客服消息
     * @param obj
     * @param ACCESS_TOKEN
     * @return
     */
    public static String sendMessage(Map<String,Object> obj,String ACCESS_TOKEN){
    	JSONObject json = JSONObject.fromObject(obj);
    	System.out.println("--------发送客服消息---------json-----"+json.toString());
    	// 调用接口获取access_token
    	String url = send_message_url.replace("ACCESS_TOKEN",ACCESS_TOKEN);
    	JSONObject jsonObject = WeixinHttpUtils.httpRequest(url, "POST", json.toString());
    	return jsonObject.toString();
    }

}