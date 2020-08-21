import json

from app.service.tosca.vo.tgif.self import Self
from app.service.tosca.vo.tgif.stream import Stream
from app.service.tosca.vo.tgif.tgif import Tgif
from app.service.tosca.vo.tgif.service import Service
from app.service.tosca.vo.tgif.auxiliary import Auxiliary
from app.service.tosca.vo.tgif.request import Request
from app.service.tosca.vo.tgif.response import Response
from app.service.tosca.vo.tgif.provide import Provide
from app.service.tosca.vo.tgif.call import Call
import logging as logger


def populate_tgif(version, metaDataJson: dict, protobufJson: dict):
    logger.debug('--------  populateTgif() Begin ------------')
    solutionName = metaDataJson.get('name', 'Key not found')
    description = metaDataJson.get('description', '')
    COMPONENT_TYPE = 'Docker'
    # Set Self
    self = Self(version, solutionName, description, COMPONENT_TYPE)
    # Set empty Stream
    streams = Stream(None, None)
    # Set services
    scalls = get_calls(protobufJson)
    sprovides = get_provides(protobufJson)

    services = Service(scalls, sprovides)
    # Set array of Parameters
    parameters = []
    # Set Auxilary
    auxiliary = Auxiliary(None)
    # Set array of artifacts
    artifacts = []
    result = Tgif(self, streams, services, parameters, auxiliary, artifacts)
    logger.debug('--------  populateTgif() End ------------')
    return result


def get_provides(protobufJson: dict):
    service = protobufJson.get('service')
    listOfOperations = service.get('listOfOperations')
    listOfMessages = protobufJson.get('listOfMessages')
    result = []
    # 这里Iterator中的元素类型为Object，产生了赋值错误，所以将泛型类型改为Object，再迭代过程中再将Objuct转为字符串再转为JSONObject。
    itr = iter(listOfOperations)
    for operation in itr:
        # 获取一个数据并绑定到x
        operationName = operation.get('operationName')
        # Construct the format : for each output Message get the message name and then
        # get collect the message details
        listOfInputMessages = operation.get('listOfInputMessages')
        inputMsgItr = iter(listOfInputMessages)
        inputMsgJsonArray = []
        for inputMessage in inputMsgItr:
            inputMsgName = inputMessage.get('inputMessageName')
            inputMsgJsonArray.append(get_msg_json(inputMsgName, listOfMessages))

        p_request = Request(inputMsgJsonArray, '')
        p_response = Response([], '')
        provide = Provide(operationName, p_request, p_response)
        result.append(provide)
    return result


def get_calls(protobufJson):
    service = protobufJson.get('service')
    listOfOperations = service.get('listOfOperations')
    listOfMessages = protobufJson.get('listOfMessages')
    result = []

    itr = iter(listOfOperations)
    for operation in itr:
        operationName = operation.get('operationName')
        # Construct the format : for each output Message get the message name and then
        # get collect the message details
        listOfOutputMessages = operation.get('listOfOutputMessages')
        outputMsgJsonArray = []
        outputMsgItr = iter(listOfOutputMessages)

        for outputMessage in outputMsgItr:
            outputMsgName = outputMessage.get('outPutMessageName')
            outputMsgJsonArray.append(get_msg_json(outputMsgName, listOfMessages))
        request = Request(outputMsgJsonArray, '')
        response = Response([], '')
        call = Call(operationName, request, response)
        result.append(call)
    return result


def get_msg_json(msgName, listOfMessages):
    itr = iter(listOfMessages)
    for message in itr:
        messageName = message.get('messageName')
        if messageName == msgName:
            logger.debug('message : {}'.format(json.dumps(message)))
            break

    if message is None:
        logger.error('listOfMessages is none')
        raise Exception('listOfMessages is none')
    return message
