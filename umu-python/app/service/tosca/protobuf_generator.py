import json
import logging as logger

from app.service.tosca.vo.protobuf.input_message import InputMessage
from app.service.tosca.vo.protobuf.operation import Operation
from app.service.tosca.vo.protobuf.output_message import OutputMessage
from app.service.tosca.vo.protobuf.proto_buf_class import ProtoBufClass
from app.service.tosca.vo.protobuf.message_body import MessageBody
from app.service.tosca.vo.protobuf.messageargument_list import MessageArgumentList
from app.service.tosca.vo.protobuf.complex_type import ComplexType
from app.service.tosca.vo.protobuf.service import Service


def cmp_msg_list(a: MessageArgumentList):
    return a['tag']


def construct_syntax(line: str):
    logger.debug('-------------- constructSyntax() strated ---------------')
    removequotes = ''
    try:
        if line.startswith('syntax'):
            fields = line.split('=')
            removeSemicolon = fields[1].replace(';', '')
            trimString = removeSemicolon.strip()
            removequotes = trimString.replace('^\'|\'$', '')

    except Exception as ex:
        logger.error('  Exception Occured  constructSyntax() {}'.format(repr(ex)))
    logger.debug('-------------- constructSyntax() end ---------------')
    return removequotes


def construct_package(line: str):
    # logger.debug('-------------- constructPackage() strated ---------------')
    fields = line.split(' ')
    # logger.debug('-------------- constructPackage() end ---------------')
    return fields[1].replace(';', '')


class ProtobufGenerator:
    def __init__(self):
        self.is_message = False
        self.is_itservice = False
        self.service = Service()
        self.proto_buf_class = ProtoBufClass()
        self.message_body_list = []
        self.message_body = MessageBody()
        self.message_argument_list = []
        self.list_of_input_messages = []
        self.list_of_input_and_output_message = []
        self.list_of_output_messages = []
        self.list_of_option = []

    def create_proto_json(self, schemaFile: str):
        # 解析字符串称为类属性，然后生成json字符串
        self.proto_buf_class = ProtoBufClass()
        self.message_body_list = []
        self.list_of_input_and_output_message = []
        self.list_of_option = []

        with open(schemaFile, 'r') as file:
            text_lines = file.readlines()
            for sCurrentLine in text_lines:
                self.parse_line(sCurrentLine.replace('\t', ''))
            proto_buf_to_json_string = json.dumps(self.proto_buf_class)
            try:
                expendedmessage_body_list = self.construct_sub_message_body(proto_buf_to_json_string)
                self.proto_buf_class['listOfMessages'] = expendedmessage_body_list
                proto_buf_to_json_string = json.dumps(self.proto_buf_class)
            except Exception as ex:
                logger.error(repr(ex))
        return proto_buf_to_json_string

    def parse_line(self, line: str):
        # 该方法抛出的异常为designStodio自己实现的ServiceException改为Exception
        try:
            # construct syntax
            if 'syntax' in line:
                self.proto_buf_class['syntax'] = construct_syntax(line)
            # construct package
            if line.startswith('package'):
                self.proto_buf_class['packageName'] = construct_package(line)
            # 参数文件中这个地方的值为False
            # start package
            if line.startswith('message'):
                # logger.debug('-------------- costructMessage() strated ---------------')
                self.is_message = False
                self.message_body = MessageBody()
                self.message_body = self.costruct_message(line, self.message_body, None)
            elif self.is_message and '}' not in line:
                self.message_argument_list = []
                self.message_body = self.costruct_message(line, self.message_body, self.message_argument_list)
            elif self.is_message and '}' in line:
                self.message_body_list.append(self.message_body)
                self.proto_buf_class['listOfMessages'] = self.message_body_list
                self.is_message = False
                # logger.debug('-------------- costructMessage() end ---------------')
            if line.startswith('service'):
                # logger.debug('-------------- constructService() started ---------------')
                self.service = Service()
                self.service = self.construct_service(line, self.service)

            elif self.is_itservice and line and '}' not in line:
                operation = Operation()
                line = line.replace(';', '').replace('\t', '').strip()
                line1 = line.split('returns')[0]
                operationType = line1.split(' ', 1)[0].strip()

                line2 = line1.split(' ', 1)[1].replace(' ', '').replace('(', '%br%').replace(')', '').strip()
                operationName = line2.split('%br%')[0].strip()
                input_para_str = line2.split('%br%')[1].strip()
                out_put_para_str = line.split('returns')[1].replace('(', '').replace(')', '').strip()
                operation['operationType'] = operationType
                operation['operationName'] = operationName
                self.list_of_input_messages = self.construct_input_message(input_para_str)
                self.list_of_output_messages = self.construct_output_message(out_put_para_str)

                operation['listOfInputMessages'] = self.list_of_input_messages
                operation['listOfOutputMessages'] = self.list_of_output_messages
                if self.service['listOfOperations']:
                    self.service['listOfOperations'].append(operation)
                else:
                    listOfOperation = [operation]
                    self.service['listOfOperations'] = listOfOperation
                    self.proto_buf_class['service'] = self.service
            elif self.is_itservice and line and '}' in line:
                self.is_itservice = False
                logger.debug('-------------- constructService() end ---------------')
        except Exception as ex:
            logger.error('Exception Occured  parseLine() {}'.format(repr(ex)))
            # throw ServiceException(' --------------- Exception Occured while parsing protobuf --------------',

    def costruct_message(self, line: str, messageBody: MessageBody, msg_lists: []):
        line = line.strip()
        try:
            if line.startswith('message'):
                fields = line.split(' ')
                messageBody['messageName'] = fields[1]
            if self.is_message:
                if line.endswith(';') or ';' in line:
                    fields = line.split(' ')
                    msg_list_ins = MessageArgumentList()
                    for i in range(0, len(fields)):
                        if fields[i]:
                            if len(fields) == 5:  # count total number of
                                # token in line.
                                msg_list_ins['role'] = fields[i]
                                msg_list_ins['type'] = fields[i + 1]
                                msg_list_ins['name'] = fields[i + 2]
                                msg_list_ins['tag'] = fields[i + 4].replace(';', '')
                            else:
                                msg_list_ins['role'] = ''
                                msg_list_ins['type'] = fields[i]
                                msg_list_ins['name'] = fields[i + 1]
                                msg_list_ins['tag'] = fields[i + 3].replace(';', '')
                            break
                    if messageBody['messageargumentList']:
                        messageBody['messageargumentList'].append(msg_list_ins)
                        messageBody['messageargumentList'].sort(key=cmp_msg_list)
                    else:
                        msg_lists.append(msg_list_ins)
                        messageBody['messageargumentList'] = msg_lists
            if '}' in line:
                self.is_message = False
            else:
                self.is_message = True
        except Exception as ex:
            logger.error(' Exception Occured  costructMessage() {}'.format(repr(ex)))
        return messageBody

    def construct_service(self, line: str, service: Service):
        try:
            fields = line.split(' ')
            service['name'] = fields[1]
            if '}' in line:
                self.is_itservice = False
            else:
                self.is_itservice = True
        except Exception as ex:
            logger.error(' Exception Occured  constructService() {}'.format(repr(ex)))
        return service

    def construct_input_message(self, inputParameterString: str):
        logger.debug('-------------- constructInputMessage() strated ---------------')
        try:
            in_put_parameter_array = inputParameterString.split(',')
            for i in range(0, len(in_put_parameter_array)):
                inputMessage = InputMessage()
                inputMessage['inputMessageName'] = in_put_parameter_array[i]
                self.list_of_input_messages.append(inputMessage)
                self.list_of_input_and_output_message.append(inputMessage['inputMessageName'])
        except Exception as ex:
            logger.error('Exception Occured  constructInputMessage() {}'.format(repr(ex)))

        logger.debug('-------------- constructInputMessage() end ---------------')
        return self.list_of_input_messages

    def construct_output_message(self, out_put_para_str: str):
        logger.debug('-------------- constructOutputMessage() strated ---------------')
        self.list_of_output_messages = []
        try:
            out_put_parameter_array = out_put_para_str.split(',')
            for i in range(0, len(out_put_parameter_array)):
                outputMessage = OutputMessage()
                outputMessage['outPutMessageName'] = out_put_parameter_array[i]
                self.list_of_output_messages.append(outputMessage)
                self.list_of_input_and_output_message.append(outputMessage['outPutMessageName'])
        except Exception as ex:
            logger.error('constructOutputMessage() end {}'.format(repr(ex)))
        logger.debug('-------------- constructOutputMessage() end ---------------')
        return self.list_of_output_messages

    def construct_sub_message_body(self, proto_buf_to_json_string: str):
        logger.debug('-------------- constructconstructSubMessageBody() strated ---------------')
        ex_msg_body_list = []
        ex_childmsg_body_list = []
        try:
            parent_not_exist_list = []
            dup_proto_buf_class = json.loads(proto_buf_to_json_string)
            src_msg_body = self.proto_buf_class['listOfMessages']
            dest_msg_body = dup_proto_buf_class['listOfMessages']

            basic_protobuf_types = 'string,int32,int64'
            basic_protobuf_types_list = basic_protobuf_types.split(',')

            is_sub_message_found = False
            for i in range(0, len(src_msg_body)):
                src_msg_name = src_msg_body[i]['messageName']
                msg_list = src_msg_body[i]['messageargumentList']
                comlpex_msg_list = []

                for j in range(0, len(dest_msg_body)):
                    dest_msg_name = dest_msg_body[j]['messageName']  # change
                    if src_msg_name == dest_msg_name:
                        continue
                    parent_name_msg_list = dest_msg_body[j]['messageargumentList']
                    if parent_name_msg_list and len(parent_name_msg_list) > 0:
                        for k in range(0, len(parent_name_msg_list)):
                            ptype = parent_name_msg_list[k]['type']
                            if ptype not in basic_protobuf_types_list and ptype == src_msg_name:
                                is_sub_message_found = True
                                expended_msg_body = None
                                msg_list_inst_list = None
                                message = ''
                                if ex_msg_body_list and len(ex_msg_body_list) > 0:
                                    for ei in range(0, len(ex_msg_body_list)):
                                        expended_msg_body = ex_msg_body_list[ei]
                                        message = expended_msg_body['messageName']
                                        if message == dest_msg_body[j]['messageName']:
                                            msg_list_inst_list = expended_msg_body['messageargumentList']
                                            break
                                        else:
                                            msg_list_inst_list = []
                                            expended_msg_body = MessageBody()
                                else:
                                    msg_list_inst_list = []
                                    expended_msg_body = MessageBody()
                                if dest_msg_name in ex_childmsg_body_list:
                                    msg_list_inst = MessageArgumentList()
                                    complexType = ComplexType()
                                    for m_body0 in ex_msg_body_list:
                                        m_list = m_body0['messageargumentList']
                                        msg_arg_list = None
                                        msg_arg_list_index = 0
                                        for jj in range(0, len(m_list)):
                                            msg_arg_list = m_body0['messageargumentList'][jj]
                                            if msg_arg_list['complexType']:
                                                complexType = msg_arg_list['complexType']
                                                msg_arg_list_index = jj

                                        complex_msg_list = complexType['messageargumentList']
                                        for ii in range(0, len(complex_msg_list)):
                                            msg_list = complex_msg_list[ii]
                                            if msg_list['type'] == src_msg_name:
                                                parentTag = msg_list['tag']
                                                comlpex_msg_list20 = []
                                                complex_type_object = ComplexType()
                                                for ll in range(0, len(msg_list)):
                                                    msg_list12 = msg_list[ll]
                                                    complex_msg_inst = MessageArgumentList()
                                                    complex_msg_inst['role'] = msg_list12['role']
                                                    complex_msg_inst['type'] = msg_list12['type']
                                                    complex_msg_inst['name'] = msg_list12['name']
                                                    complex_msg_inst['tag'] = parentTag + '.' + msg_list12['tag']
                                                    comlpex_msg_list20.append(complex_msg_inst)

                                                complex_type_object['messageName'] = src_msg_name
                                                complex_type_object['messageargumentList'] = comlpex_msg_list20
                                                msg_list_inst['role'] = msg_list['role']
                                                msg_list_inst['type'] = msg_list['type']
                                                msg_list_inst['complexType'] = complex_type_object
                                                msg_list_inst['name'] = msg_list['name']
                                                msg_list_inst['tag'] = msg_list['tag']
                                                complex_msg_list.set(ii, msg_list_inst)
                                                complexType['messageargumentList'] = complex_msg_list
                                                msg_arg_list['complexType'] = complexType
                                                m_body0['messageargumentList'][msg_arg_list_index] = msg_arg_list
                                else:
                                    expended_msg_body['messageName'] = dest_msg_body[j]['messageName']
                                    msg_list_inst = MessageArgumentList()
                                    msg_list_inst_symple_pre_list = []
                                    msg_list_inst_symple_post_list = []
                                    simpleTypePreIndex = k

                                    if simpleTypePreIndex > 0:
                                        for kk in range(0, k):
                                            msg_list_inst_symple = MessageArgumentList()
                                            msg_list_inst_symple['role'] = parent_name_msg_list[kk]['role']
                                            msg_list_inst_symple['type'] = parent_name_msg_list[kk]['type']
                                            msg_list_inst_symple['name'] = parent_name_msg_list[kk]['name']
                                            msg_list_inst_symple['tag'] = parent_name_msg_list[kk]['tag']
                                            msg_list_inst_symple_pre_list.append(msg_list_inst_symple)

                                    msg_list_inst['role'] = parent_name_msg_list[k]['role']
                                    msg_list_inst['type'] = parent_name_msg_list[k]['type']
                                    parentTag = parent_name_msg_list[k]['tag']
                                    complexType = ComplexType()
                                    complexType['messageName'] = src_msg_name
                                    for ll in range(0, len(msg_list)):
                                        msg_list12 = msg_list[ll]
                                        complex_msg_inst = MessageArgumentList()
                                        complex_msg_inst['role'] = msg_list12['role']
                                        complex_msg_inst['type'] = msg_list12['type']
                                        complex_msg_inst['name'] = msg_list12['name']
                                        complex_msg_inst['tag'] = parentTag + '.' + msg_list12['tag']
                                        comlpex_msg_list.append(complex_msg_inst)

                                    complexType['messageargumentList'] = comlpex_msg_list
                                    msg_list_inst['complexType'] = complexType
                                    msg_list_inst['name'] = parent_name_msg_list[k]['name']
                                    msg_list_inst['tag'] = parent_name_msg_list[k]['tag']
                                    if msg_list_inst_list and len(msg_list_inst_list) > 0:
                                        msg_list_inst_list.append(msg_list_inst)
                                        expended_msg_body['messageargumentList'] = msg_list_inst_list
                                    else:
                                        msg_list_inst_list.extend(msg_list_inst_symple_pre_list)
                                        msg_list_inst_list.append(msg_list_inst)
                                        msg_list_inst_list.extend(msg_list_inst_symple_post_list)
                                        expended_msg_body['messageargumentList'] = msg_list_inst_list
                                    ex_childmsg_body_list.append(src_msg_body[i]['messageName'])
                                    if message != dest_msg_body[j]['messageName']:
                                        ex_msg_body_list.append(expended_msg_body)
                                break
                            else:
                                is_sub_message_found = False
                    if is_sub_message_found:
                        is_sub_message_found = True
                        break
                if not is_sub_message_found:
                    parent_not_exist_list.append(src_msg_body[i])
            size = len(parent_not_exist_list)
            dup_msg_is_found = False
            for ij in range(0, size):
                dup_msg = parent_not_exist_list[ij]['messageName']
                for j in range(0, len(ex_msg_body_list)):
                    if dup_msg == ex_msg_body_list[j]['messageName']:
                        dup_msg_is_found = True
                        break
                    else:
                        dup_msg_is_found = False
                if not dup_msg_is_found:
                    ex_msg_body_list.append(parent_not_exist_list[ij])
                    dup_msg_is_found = False
        except Exception as ex:
            logger.error(repr(ex))

        logger.debug('-------------- constructconstructSubMessageBody() end ---------------')
        return ex_msg_body_list
