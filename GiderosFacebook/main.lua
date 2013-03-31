require "facebook"

facebook:addEventListener(Event.LOGIN_COMPLETE, function()
	print("login successful")
	facebook:extendAccessTokenIfNeeded()
end)

facebook:setAppId("352852401492555");
facebook:authorize();

local text = TextField.new(nil, "post to wall")
text:setScale(3)
text:setPosition(10, 50)
stage:addChild(text)
text:addEventListener(Event.TOUCHES_BEGIN, function(e)
	if text:hitTestPoint(e.touch.x, e.touch.y) and facebook:isSessionValid() then
		facebook:dialog("feed", {
			link = "http://giderosmobile.com", 
			picture = "http://www.giderosmobile.com/wp-content/uploads/2012/06/gideros-mobile-small.png", 
			name = "GiderosMobile.com", 
			caption = "Awesome tool", 
			description = "Check out this awesome product"
		})
	end
end)


local text2 = TextField.new(nil, "Make api request")
text2:setScale(3)
text2:setPosition(10, 150)
stage:addChild(text2)
text2:addEventListener(Event.TOUCHES_BEGIN, function(e)
	if text2:hitTestPoint(e.touch.x, e.touch.y) and facebook:isSessionValid() then
		facebook:graphRequest("me")
	end
end)

facebook:addEventListener(Event.REQUEST_COMPLETE, function(e)
	print(e.response)
end)

facebook:addEventListener(Event.REQUEST_ERROR, function(e)
	print(e.errorDescription)
end)