#!/usr/bin/env python
# encoding: utf-8
# @author: cc <chai_pengfei@163.com>

import requests
import re

def find_No(htmlPage, pins = None):
    """ """
    if pins == None:
        pins = []
    prog = re.compile(r'pin_id":(\d)+')
    for p in prog.finditer(htmlPage):
        pin = p.group()
        pins.append(pin[8:])
    return pins

def find_image(htmlPage, images = None):
    """ """
    if images == None:
        images = []
    prog = re.compile(r'"key":"(\w)*-(\w)*"')
    for p in prog.finditer(htmlPage):
        imgUrl = p.group()
        images.append(imgUrl[7:-1]+"_fw658")
    return images

def make_ajax_url(No):
    return "http://huaban.com/favorite/beauty/?i5p998kw&max=" + No + "&limit=20&wfl=1"

def load_homePage():
    homeUrl = "http://huaban.com/favorite/beauty/"
    req = requests.get(url = homeUrl)
    return req.content

def load_more(maxNo):
    req = requests.get(url = make_ajax_url(maxNo))
    return req.content

