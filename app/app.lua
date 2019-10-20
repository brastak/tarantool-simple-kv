local log = require('log')
local json = require('json')
local storage = require('storage')
local string = require('string')
local table = require('table')

box.cfg { 
  listen = 3301
}
storage:init()

-- CRUD operations exposed to the REST API
function create(request)
	log.debug('Process call for persist data with URI %s and payload %s', request.uri, request.body)
	if table.getn(string.split(request.uri, '/')) ~= 2 then
		local errorMsg = string.format('Invalid URI: %s', request.uri)
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end
	if request.body == nil then
		local errorMsg = 'Empty payload'
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

	local status, payload = pcall(json.decode, request.body)
	if not status then
		log.error('Invalid payload %s: %s', request.body, payload)
		return { code = 400, body = payload }
	end
	if payload['key'] == nil or payload['value'] == nil then
		local errorMsg = 'Invalid payload: miss some of required attributes [key, value] (null values are not allowed)'
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end
	if type(payload['key']) ~= 'string' then
		local errorMsg = string.format('Invalid type for key: only string supported but %s found', type(payload['key']))
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

    local data = storage:create(payload['key'], payload['value'])
    if data == nil then
    	local errorMsg = string.format('Duplicated key %s', payload['key'])
		log.info(errorMsg)
	    return { code = 409, body = errorMsg }
    end

	local json_data = json.encode(data)
	log.info('Persist data with key %s: %s', payload['key'], json_data)
    return { code = 201, body = json_data }
end

function retrieve(request)
	log.debug('Process call for retrieve data with URI %s', request.uri)
	local key = getKey(request.uri)
	if key == nil then
		local errorMsg = string.format('Invalid URI: %s', request.uri)
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

	data = storage:retrieve(key)
	if data == nil then
	    return notFound(key)
	else
		local json_data = json.encode(data)
		log.info('Retrive data for key %s: %s', key, json_data)
	    return { code = 200, body = json_data }
    end
end

function update(request)
	log.debug('Process call for update data with URI %s and payload %s', request.uri, request.body)
	local key = getKey(request.uri)
	if key == nil then
		local errorMsg = string.format('Invalid URI: %s', request.uri)
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end
	if request.body == nil then
		local errorMsg = 'Empty payload'
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

	local status, payload = pcall(json.decode, request.body)
	if not status then
		log.error('Invalid payload %s: %s', request.body, payload)
		return { code = 400, body = payload }
	end
	if payload == nil then
		local errorMsg = 'Null payload: %s', request.body
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

	data = storage:update(key, payload)
	if data == nil then
	    return notFound(key)
	else
		local json_data = json.encode(data)
		log.info('Update key %s with new data: %s', key, json_data)
	    return { code = 200, body = json_data }
    end
end

function delete(request)
	log.debug('Process call for delete data with URI %s', request.uri)
	local key = getKey(request.uri)
	if key == nil then
		local errorMsg = string.format('Invalid URI: %s', request.uri)
		log.error(errorMsg)
		return { code = 400, body = errorMsg }
	end

    deleted = storage:delete(key)
    if deleted then
		log.info('Delete data for key %s', key)
	    return { code = 204, body = nil }
	else
	    return notFound(key)
    end

end

function getKey(uri)
	local uri_tokens = string.split(uri, '/')
	if table.getn(uri_tokens) ~= 3 then
		return nil
	else 
		return uri_tokens[3]
	end
end

function notFound(key)
	local errorMsg = string.format('Key %s not found', key)
	log.error(errorMsg)
	return { code = 404, body = errorMsg }
end