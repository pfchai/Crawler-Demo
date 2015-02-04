#!/usr/bin/env python
# encoding: utf-8
# @author: cc <chai_pengfei@163.com>

import requests
import re

class HuabanCrawler():
    """ """

    def __init__(self):
        """ """
        self.homeUrl = "http://huaban.com/favorite/beauty/"
        self.appPins = []
        self.imageUrls = []

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
            self.appPins.append(i['pin_id'])
            self.imageUrls.append("http://img.hb.aicdn.com/" + i["file"]["key"] + "_fw658")

    def __save_image(self, imageName, content):
        with open(imageName+'.jpeg', 'wb') as fp:
            fp.write(content)

    def downImages(self):
        """ """
        self.__process_data(self.__load_homePage())
        for key, url in zip(self.appPins, self.imageUrls):
            print key, url
            req = requests.get(url)
            self.__save_image(str(key), req.content)



if __name__ == '__main__':
    hc = HuabanCrawler()
    hc.downImages()
