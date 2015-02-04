#!/usr/bin/env python
# encoding: utf-8
# @author: cc <chai_pengfei@163.com>

import requests
import re
import os
import os.path

class HuabanCrawler():
    """ """

    def __init__(self):
        """ """
        self.homeUrl = "http://huaban.com/favorite/beauty/"
        self.images = []
        if not os.path.exists('./images'):
            os.mkdir('./images')

    def __load_homePage(self):
        return requests.get(url = self.homeUrl).content

    def __make_ajax_url(self, No):
        """ """
        return self.homeUrl + "?i5p998kw&max=" + No + "&limit=20&wfl=1"

    def __load_more(self, maxNo):
        return requests.get(url = self.__make_ajax_url(maxNo)).content

    def __process_data(self, htmlPage):
        """ """
        prog = re.compile(r'app\.page\["pins"\].*')
        appPins = prog.findall(htmlPage)
        # covert javascript null to None
        null = None
        result = eval(appPins[0][19:-1])
        for i in result:
            info = {}
            info['id'] = str(i['pin_id'])
            info['url'] = "http://img.hb.aicdn.com/" + i["file"]["key"] + "_fw658"
            info['type'] = i["file"]["type"][6:]
            self.images.append(info)

    def __save_image(self, imageName, content):
        with open(imageName, 'wb') as fp:
            fp.write(content)

    def get_image_info(self, num):
        """ """
        self.__process_data(self.__load_homePage())
        for i in range((num-1)/20):
            self.__process_data(self.__load_more(self.images[-1]['id']))

    def down_images(self):
        """ """
        for image in self.images:
            req = requests.get(image["url"])
            imageName = os.path.join("./images", image["id"] + "." + image["type"])
            self.__save_image(imageName, req.content)


if __name__ == '__main__':
    hc = HuabanCrawler()
    hc.get_image_info(21)
    hc.down_images()
