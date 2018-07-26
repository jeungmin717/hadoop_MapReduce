# hadoop_MapReduce

Requirements:
가상 분산(또는 분산) 모드 환경에서 문서 검색 엔진 개발

1) 다음과 같은 빅데이터를 만든 후, 하둡으로 역 인덱스를 구함.
- 빅데이터 : “문서1 …컨텐츠… 문서2 …컨텐츠… 문서3 …컨텐츠…”

2) 사용자가 검색 엔진에 단어들을 입력하면 해당 단어들이 많은 문서들 순서로 검색.
“to be question” => 문서2
“to be” => 문서1, 문서2   (문서1에 우선 순위가 있음)




Hadoop을 통한 역인덱싱

◎ 소스 코드

![image](https://user-images.githubusercontent.com/34965935/43256997-71ceb364-9109-11e8-914e-ac3c5a564ff7.png)
 - Map클래스이며 문서명을 key, 문서내용을 value값으로 받아 단어 Key값에 대한 Value값으로 {문서명,빈도수}로 파일을 mapping해줍니다.


![image](https://user-images.githubusercontent.com/34965935/43257018-88a62b80-9109-11e8-84cc-99380a5f6e47.png)

-Mapping된 데이터들은 하나의 리듀서로 모아서,각각의 키 값의 문서명과 빈도수를 통합해서 형식에 맞게 출력한다.


![image](https://user-images.githubusercontent.com/34965935/43257038-9a4c7358-9109-11e8-9e41-5a8be1979152.png)

- {문서이름, 빈도수}를 저장하기 위한 새로 정의한 데이터타입

![image](https://user-images.githubusercontent.com/34965935/43257046-a148c936-9109-11e8-8f63-0d0f7931dd3a.png)

◎ 입력 파일

![image](https://user-images.githubusercontent.com/34965935/43257053-a759ed6e-9109-11e8-9543-9a5883491f5e.png)

◎ 역인덱스 파일

![image](https://user-images.githubusercontent.com/34965935/43257066-ad33f888-9109-11e8-8ca4-f381ea6576b6.png)

◎ 실행 화면

![image](https://user-images.githubusercontent.com/34965935/43257070-b1d04acc-9109-11e8-8b87-0ce787564f40.png)
