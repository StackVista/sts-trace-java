FROM circleci/openjdk:8

RUN sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0x219BD9C9
RUN echo 'deb http://repos.azulsystems.com/debian stable main' | sudo tee -a /etc/apt/sources.list.d/zulu.list
RUN sudo apt-get update
RUN sudo apt-get install zulu-7 zulu-9 zulu-10 zulu-11
