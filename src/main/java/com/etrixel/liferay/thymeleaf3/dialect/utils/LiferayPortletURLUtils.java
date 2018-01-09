package com.etrixel.liferay.thymeleaf3.dialect.utils;

import static com.liferay.portal.kernel.portlet.LiferayPortletMode.ABOUT;
import static com.liferay.portal.kernel.portlet.LiferayPortletMode.CONFIG;
import static com.liferay.portal.kernel.portlet.LiferayPortletMode.EDIT_DEFAULTS;
import static com.liferay.portal.kernel.portlet.LiferayPortletMode.EDIT_GUEST;
import static com.liferay.portal.kernel.portlet.LiferayPortletMode.PREVIEW;
import static com.liferay.portal.kernel.portlet.LiferayPortletMode.PRINT;
import static com.liferay.portal.kernel.portlet.LiferayWindowState.EXCLUSIVE;
import static com.liferay.portal.kernel.portlet.LiferayWindowState.POP_UP;
import static javax.portlet.PortletMode.EDIT;
import static javax.portlet.PortletMode.HELP;
import static javax.portlet.PortletMode.VIEW;
import static javax.portlet.WindowState.MAXIMIZED;
import static javax.portlet.WindowState.MINIMIZED;
import static javax.portlet.WindowState.NORMAL;

import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.PortletURLFactoryUtil;

public class LiferayPortletURLUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiferayPortletURLUtils.class);

    private static final PortletMode[] PORTLET_MODES =
            {VIEW, EDIT, HELP, ABOUT, CONFIG, EDIT_DEFAULTS, EDIT_GUEST, PREVIEW, PRINT};

    private static final WindowState[] WINDOW_STATES = {NORMAL, MAXIMIZED, MINIMIZED, EXCLUSIVE, POP_UP};

    public static LiferayPortletURL createURL(Map<String, Object> params, PortletRequest request) {
        return createURL(params, PortalUtil.getHttpServletRequest(request));
    }

    public static LiferayPortletURL createURL(Map<String, Object> params, HttpServletRequest request) {
        PortletRequest portletRequest = (PortletRequest) request.getAttribute("javax.portlet.request");

        String portletId = getPortletId(getValueFromPref(portletRequest, getStringOrNull(params.get("portletIdPref")),
                getStringOrNull(params.get("portletId"))), request);

        long plid = getPlid(
                getStringOrNull(params.get("plid")), getValueFromPref(portletRequest,
                        getStringOrNull(params.get("portletUrlPref")), getStringOrNull(params.get("portletUrl"))),
                request);

        String lifecycle = getLifecycle(getStringOrNull(params.get("lifecycle")));

        LiferayPortletURL portletURL = PortletURLFactoryUtil.create(request, portletId, plid, lifecycle);

        try {
            portletURL.setWindowState(getWindowState(getStringOrNull(params.get("windowState"))));
        } catch (WindowStateException e) {
            LOGGER.error("Cannot parse windowState: {}", params.get("windowState"), e);
        }

        try {
            portletURL.setPortletMode(getPortletMode(getStringOrNull(params.get("portletMode"))));
        } catch (PortletModeException e) {
            LOGGER.error("Cannot parse portletMode: {}", params.get("portletMode"), e);
        }

        if (params.containsKey("action")) {
            portletURL.setParameter("javax.portlet.action", defaultIfNull(params.get("action"), "").toString(), false);
            portletURL.setLifecycle(PortletRequest.ACTION_PHASE);
        }

        if (params.containsKey("resource")) {
            portletURL.setResourceID(defaultIfNull(params.get("resource"), "").toString());
            portletURL.setLifecycle(PortletRequest.RESOURCE_PHASE);
        }

        params.remove("portletId");
        params.remove("portletIdPref");
        params.remove("plid");
        params.remove("portletUrl");
        params.remove("portletUrlPref");
        params.remove("lifecycle");
        params.remove("windowState");
        params.remove("portletMode");
        params.remove("action");
        params.remove("resource");

        for (Entry<String, Object> entry : params.entrySet()) {
            portletURL.setParameter(entry.getKey(), defaultIfNull(entry.getValue(), "").toString(), true);
        }

        return portletURL;
    }

    private static long getPlid(String plid, String portletUrl, HttpServletRequest request) {
        long id = 0;

        try {
            if (plid != null) {
                id = Long.parseLong(plid);
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Cannot parse plid: {}", plid, e);
        }

        if (id > 0) {
            return id;
        }

        try {
            if (portletUrl != null) {
                id = getPlidFromUrl(portletUrl, request);
            }
        } catch (Exception e) {
            LOGGER.error("Plid not found for url: {}", portletUrl, e);
        }

        if (id > 0) {
            return id;
        }

        return getCurrentPlid(request);
    }

    private static long getPlidFromUrl(String portletUrl, HttpServletRequest request)
            throws SystemException, PortalException {
        Layout friendlyURLLayout =
                LayoutLocalServiceUtil.getFriendlyURLLayout(PortalUtil.getScopeGroupId(request), false, portletUrl);
        return friendlyURLLayout.getPlid();
    }

    private static long getCurrentPlid(HttpServletRequest request) {
        ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
        return themeDisplay.getLayout().getPlid();
    }

    private static String getPortletId(String portletId, HttpServletRequest request) {
        return defaultIfNull(portletId, (String) request.getAttribute(WebKeys.PORTLET_ID));
    }

    private static String getLifecycle(String lifecycle) {
        return defaultIfNull(lifecycle, PortletRequest.RENDER_PHASE);
    }

    private static WindowState getWindowState(String windowState) {
        return getValue(WINDOW_STATES, windowState, WindowState.NORMAL);
    }

    private static PortletMode getPortletMode(String portletMode) {
        return getValue(PORTLET_MODES, portletMode, PortletMode.VIEW);
    }

    private static String getValueFromPref(PortletRequest request, String preference, String defaultValue) {
        return preference != null ? request.getPreferences().getValue(preference, defaultValue) : defaultValue;
    }

    private static String getStringOrNull(Object value) {
        return value != null ? value.toString().trim() : null;
    }

    private static <T> T defaultIfNull(T value, T defaultValue) {
        return value != null ? value : defaultValue;
    }

    private static <T> T getValue(T[] valueArray, String value, T defaultValue) {
        if (value != null) {
            String valueLc = value.toLowerCase();
            for (T val : valueArray) {
                if (val.toString().equalsIgnoreCase(valueLc)) {
                    return val;
                }
            }
        }
        return defaultValue;
    }

}
