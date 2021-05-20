import smtplib
from email.mime.text import MIMEText
from email.header import Header
from app.global_data.global_data import g


def send_mail(receiver, message):
    config = g.get_central_config()
    sender = config['spring']['mail']['username']
    smtp_client = smtplib.SMTP(config['spring']['mail']['host'], config['spring']['mail']['port'])
    smtp_client.login(sender, config['spring']['mail']['password'])

    try:
        message['From'] = Header(sender, 'utf-8')
        smtp_client.sendmail(sender, [receiver], message.as_string())
        return True
    except Exception as e:
        return False


def send_activation_email(receiver, url_prefix, activation_key):
    subject = 'CubeAI注册帐号激活'
    content = '''<html>
<p>你好！</p>
<p>你正在进行注册帐号激活，请点击以下链接或者将其复制到浏览器地址栏打开网页来进行激活：</p>
<a href="{}{}">{}{}</a>
<p>请妥善保管，切勿向他人泄露！</p>
<p>谢谢！</p>
<p>CubeAI ★ 智立方</p>
<p>(本邮件为系统后台发送，请勿回复！)</p>
</html>
    '''.format(url_prefix, activation_key, url_prefix, activation_key)

    message = MIMEText(content, 'html', 'utf-8')
    message['Subject'] = Header(subject, 'utf-8')
    message['To'] = Header(receiver, 'utf-8')

    return send_mail(receiver, message)


def send_password_reset_email(receiver, url_prefix, reset_key):
    subject = 'CubeAI密码重置'
    content = '''<html>
<p>你好！</p>
<p>你正在进行密码重置，请点击以下链接或者将其复制到浏览器地址栏打开网页来重置密码：</p>
<a href="{}{}">{}{}</a>
<p>请妥善保管，切勿向他人泄露！</p>
<p>谢谢！</p>
<p>CubeAI ★ 智立方</p>
<p>(本邮件为系统后台发送，请勿回复！)</p>
</html>
    '''.format(url_prefix, reset_key, url_prefix, reset_key)

    message = MIMEText(content, 'html', 'utf-8')
    message['Subject'] = Header(subject, 'utf-8')
    message['To'] = Header(receiver, 'utf-8')

    return send_mail(receiver, message)

