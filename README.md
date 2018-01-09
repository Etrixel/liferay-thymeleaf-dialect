# [Thymeleaf](http://www.thymeleaf.org) dialect for Liferay

## Tags:

### liferay:src

This tag modifies src-attribute.

### liferay:href

This tag modifies href-attribute.

### liferay:action

This tag modifies action-attribute.

### liferay:withUrl

This tag defines local variables to use in multiple places.

## Expression objects:

### #portletPreferences

Access portlet preferences - [PortletPreferences](https://docs.liferay.com/portal/6.2/javadocs/com/liferay/portal/model/PortletPreferences.html).

## Tag Reference

**Following names are reserved for internal use**:


**portletId** - Id of the portlet where request will be sent. Default is current portlet.

**portletIdPref** - Id of the portlet from PortletPreferences.

**plid** - Portlet Layout ID. Default is current portlet **plid**.

**portletUrl** - Gets **plid** from portlet relative url.

**portletUrlPref** - Portlet relative url from PortletPreferences.

**lifecycle** - Defines portlet lifecycle to be used. Allowed values are javax.portlet.PortletRequest constants: (ACTION_PHASE, RESOURCE_PHASE, RENDER_PHASE, EVENT_PHASE). Default is RENDER_PHASE.

**windowState** - Which window state will the URL point to. Allowed values are javax.portlet.WindowState and com.liferay.portal.kernel.portlet.LiferayWindowState constants: (NORMAL, MAXIMIZED, MINIMIZED, EXCLUSIVE, POP_UP). Default is NORMAL.

**portletMode** - Mode of the portlet. Allowed values are javax.portlet.PortletMode and com.liferay.portal.kernel.portlet.LiferayPortletMode constants: (VIEW, EDIT, HELP, ABOUT, CONFIG, EDIT_DEFAULTS, EDIT_GUEST, PREVIEW, PRINT). Default is VIEW.

**action** - Shortcut for javax.portlet.action=*YOUR_VALUE*, lifecycle=ACTION_PHASE.

**resource** - Shortcut for p_p_id=*YOUR_VALUE*, lifecycle=RESOURCE_PHASE.


**Every other parameter given will be sent to portlet as query parameter**.

## Usage

### Example 1. link

```html
<a href="#" liferay:href="var1='value', var2=${definedVariable}, lifecycle='ACTION_PHASE'">
    Link
</a>
```

var1 and var2 are parameters to be sent to action phase of current portlet. Lifecycle defines that the request will be action request.

### Example 2. withUrl

```html
<p liferay:withUrl="myurl=${ {windowState='maximized', portletMode='edit'} }, save=${ { action:'save' } }">
    <a href="${myurl}">Open edit in maximized window</a>
    <a href="${save}">Save current portlet state</a>
</p>
```

### Example 3. action

```html
<a href="#" liferay:href="action='find', var1=${definedVariable}">Link</a>
```

Above is only simplified form of:

```html
<a href="#" liferay:href="javax.portlet.action='find', var1=${definedVariable}, lifecycle='ACTION_PHASE'">Link</a>
```
