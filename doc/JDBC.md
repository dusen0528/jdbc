# JDBC

- JDBC(Java Database Connectivity)는 관계형 데이터베이스에 저장된 데이터를 접근 및 조작할 수 있게 하는 자바 API
- JDBC는 자바 응용프로그램이 다양한 DBMS에 대해 일관된 API로 데이터베이스 연결, 검색, 수정, 관리 등을 할 수 있게 한다.
- 그러므로 자바 응용프로그램 개발자는 DBMS의 종류에 관계없이 JDBC API만을 이용하면 됨

![img.png](img.png)
| 구성요소 | 설명 | 역할 |
|---------|------|------|
| Java Application | 자바 응용프로그램, 자바 웹 애플리케이션 서버(tomcat, weblogic 등) | 응용 프로그램 개발자, 웹 애플리케이션 서버 개발사 |
| JDBC API | 자바 응용프로그램에서 데이터베이스를 연결하고 데이터를 제어할 수 있도록 데이터베이스 연결 및 제어를 위한 인터페이스와 클래스들 | JavaSE 개발사 (Sun microsystems, Oracle) |
| JDBC Driver Manager | 자바 응용프로그램이 사용하는 데이터베이스에 맞는 JDBC 드라이버를 찾아서 로드합니다. | JavaSE 개발사 (Sun microsystems, Oracle) |
| JDBC Driver | 각 데이터베이스 개발사에서 만든 데이터베이스 드라이버 | 데이터베이스 개발사(Oracle, MySql, PostgreSQL 등) |
