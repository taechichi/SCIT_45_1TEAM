
    let myLat;  // 내 위치 위도
    let myLong; // 내 위치 경도
    var map;  //맵
    // let myMarker;   // 내 위치 마커

    let markers = [];                  //마커배열(places 검색을 통해 나온 마커들의 배열)
    let isPanelVisible = false;     //패널 상태 false

    let shareUrl;   // 공유 Url

    let arrivalLat;     // 도착치 마커
    let arrivalLong;
    let arrivalMarker;

    let departLat;      // 출발지 마커
    let departLong;
    let departmentMarker;

    let currentMarker;

    let favBtn;
    let favImg;
    let board;

    google.maps.event.addDomListener(window, 'load', initMap);      //domlistener initmap을 실행
    // 페이지가 로드된 후 DOM 접근
    window.onload = function() {
        board = document.getElementById('board-list');  // DOM이 로드된 후에 요소를 찾음
    };

    //해시 부분을 받아 placeId를 추출
    function getLatLngFromUrl() {
        const hash = window.location.hash; //#~~~ 처럼 해시 부분 추출
        if (hash) {
            return decodeURIComponent(hash.substring(1)); // '#' 이후의 값을 추출 및 디코딩
        }
        return null;
    }

    //placeId를 받아 재검색 하여 place 객체에 필드값을 저장하는 함수
    function searchPlaceByPlaceId(placeId) {
        let userLanguage = navigator.language || navigator.userLanguage;
        const request = {
            placeId: placeId,
            fields: ['name', 'geometry', 'photo', 'place_id', 'formatted_address', 'rating'],// 원하는 필드
            language: userLanguage  // 언어설정
        };
        const service = new google.maps.places.PlacesService(map);

        service.getDetails(request, (place, status) => {
            //getdetails 메소드 실행
            //콜백 함수로 status 요청성공이면 createMarker
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                //url 접속시 마커생성
                createMarker(map, place, true);
            } else {
                console.error('Place details request failed');
            }
        });
    }

    //마커제거
    function  delMarker(){
        markers.forEach(marker => marker.setMap(null));
        markers = [];
    }

    //마커생성 함수
    function createMarker(map, place, visible=false, type = "none"){
        let markerOption;
        switch (type) {
            case "dbShelter":
                let shelterPosition = { lat: parseFloat(place.latitude), lng: parseFloat(place.longitude) };
                markerOption = {
                    placeId: place.shelterId,
                    map: map,
                    position: shelterPosition,
                    title: place.shelterName
                };
                break;
            case "myMarker":
                markerOption ={
                    map: map,
                    title: "myLocation",
                    position : place,
                    icon: {
                        url: '/img/map/myMarker.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(50, 50), // 아이콘의 크기 조정
                        origin: new google.maps.Point(0, 0), // 아이콘의 원점
                        anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
                    }
                };
                break;
            case "hospital":
                markerOption = {
                    placeId: place.place_id,
                    map: map,
                    position: place.geometry.location,
                    title: place.name,
                    placePhoto: place.photos ? place.photos[0].getUrl() : "",
                    icon: {
                        url: '/img/hospitalMarker.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(40, 40), // 아이콘의 크기 조정
                        origin: new google.maps.Point(0, 0), // 아이콘의 원점
                        anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
                    }
                };
                break;
            default:
                markerOption = {
                    placeId: place.place_id,
                    map: map,
                    position: place.geometry.location,
                    title: place.name,
                    placePhoto: place.photos ? place.photos[0].getUrl() : ""
                };
        }
        let marker = new google.maps.Marker(markerOption);

        switch (type) {
            case "myMarker":
                marker.setAnimation(google.maps.Animation.BOUNCE);
                break;
            default:
                markers.push(marker);
        }

        //마커 클릭 이벤트
        google.maps.event.addListener(marker, 'click', function (){
            showInfoPanel(marker);
            currentMarker = marker;             //현재 선택마커를 currentMarker에 저장
        });
        //visible = true 정보패널 표시
        if(visible){
            showInfoPanel(marker);
        }

        return marker;
    }

    //좌표를 주소로 전환
    function geocodeLatLng(location, callback){
        geocoder = new google.maps.Geocoder();
        geocoder.geocode({ 'location': location }, function (results, status){
            let localAddress;
            if (status === 'OK') {
                if (results[0]) {
                    localAddress = results[0].formatted_address;
                } else {
                    localAddress = "No results found";
                }
            } else {
                alert('Geocoder failed due to: ' + status);
            }
            callback(localAddress);
        });
    }

    //정보 패널을 보여주는 함수
    function showInfoPanel(marker){
        // 1. 기본 장소 정보 표시
        let placeName = marker.title;
        let photoUrl = marker.placePhoto;    //place의 첫사진을 url을 받아 저장
        let placeID = marker.placeId;
        // 각 마커에 대한 페이징 변수
        let currentPage = 0;  // 현재 페이지 번호
        const pageSize = 10;  // 한 번에 가져올 게시글 개수
        let isFetching = false;  // 데이터를 불러오는 중인지 여부
        geocodeLatLng(marker.position, function (placeAdress){
            let placeInfo = `<h3>${placeName}</h3>                  <!--- infopanel에 넣을 값 ---!>  
                                              <div style="width: 100%;height: 200px">
                                              <img src="${photoUrl}" style="width: 100%; height: 100%;"></div>
                                              <p>${placeAdress}</p>
                                              <p>추가입력예정</p>`;
            let infoPanel = document.getElementById("info-panel");
            let infoPart = document.getElementById("info_part");
            infoPart.innerHTML = placeInfo;
            infoPanel.style.display = 'block';
            isPanelVisible = true;
            document.getElementById('writeLink').setAttribute('href', `/board/write/${placeID}`);
            favMarkerCheck(currentMarker.placeId);
            board.innerHTML = '';
            // 게시글 목록 초기 로드
            getList(placeID, currentPage, pageSize, isFetching);
            currentPage++;
            // 스크롤 이벤트로 추가 페이지 로드
            window.addEventListener('scroll', function scrollHandler() {
                if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 500 && !isFetching) {
                    getList(placeID, currentPage, pageSize, isFetching);
                    currentPage++;  // 다음 페이지로 이동
                }
            });
        });
    }

    //마커배열을 확인해 마커들의 bound(경계값을 생성)
    function calculateBoundsForMarkers(markers) {
        const bounds = new google.maps.LatLngBounds();

        // 모든 마커의 위치를 bounds에 포함
        markers.forEach(marker => {
            bounds.extend(marker.getPosition());
        });

        map.fitBounds(bounds);
    }
    //현재 보고있는 화면 좌표기준으로 변경(내위치)
    function setCurrentZoom(centerPosition){
        map.setCenter(centerPosition);
        map.setZoom(17);
    }

    // nearbySearch를 사용해 병원 검색
    function searchNearbyHospitals() {
        const service = new google.maps.places.PlacesService(map);
        const request = {
            location: map.getCenter(),
            radius: 1000, // 반경 500m
            type: 'hospital' // 병원 타입으로 검색
        };

        // 검색 결과 처리
        service.nearbySearch(request, (results, status) => {
            if (status === google.maps.places.PlacesServiceStatus.OK) {
                // 기존 마커 제거
                delMarker();
                alert(results[0].name);

                // 검색된 병원 정보 마커로 표시
                results.forEach(place => {
                    let marker = createMarker(map, place,false,"hospital");
                    markers.push(marker);
                });
                calculateBoundsForMarkers(markers);  // 마커들로 경계 설정
            } else {
                console.error('병원 검색에 실패했습니다:', status);
            }
        });
    }

    // DB에 저장된 대피소 정보를 불러오는 함수
    function fetchNearbyShelters() {
        const center = map.getCenter(); //보는 지도의 중심좌표 획득

        delMarker();

        // 서버에 현재 위치 정보를 전송 (latitude, longitude)
        fetch('/api/nearby-shelters', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                latitude: center.lat(), // 현재 위치 위도
                longitude: center.lng(), // 현재 위치 경도
            })
        })
            .then(response => response.json()) // 서버로부터 JSON 응답 받기
            .then(data => {

                if (data && data.length > 0) {
                    // 대피소 데이터가 있으면 지도에 표시
                    data.forEach(place => {
                         console.log("확인데이터",place);
                        let marker = createMarker(map, place,false,"dbShelter"); // 대피소 마커 생성 함수
                        markers.push(marker);
                        calculateBoundsForMarkers(markers);
                    });
                } else {
                    console.log('500m 내 대피소가 없습니다.');
                }
            })
            .catch(error => {
                console.error('대피소 데이터를 불러오는 중 오류 발생:', error);
            });
    }

    //즐겨찾기 추가
    function favMarker(placeID) {
        fetch('/addFavorite/' + encodeURIComponent(placeID), {
            method: 'POST'
        })
            .then(response => {
                if (response.ok) {
                    console.log('즐겨찾기에 추가되었습니다.');
                } else {
                    console.error('즐겨찾기 추가에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }

    //즐겨찾기 삭제
    function favMarkerRemove(placeID) {
        fetch('/removeFavorite/' + encodeURIComponent(placeID), {
            method: 'POST'
        })
            .then(response => {
                if (response.ok) {
                    console.log('즐겨찾기가 삭제되었습니다..');
                } else {
                    console.error('즐겨찾기 삭제에 실패했습니다.');
                }
            })
            .catch(error => {
                console.error('Error:', error);
            });
    }
    //즐겨찾기 확인
    function favMarkerCheck(placeID){

        fetch(`/isFavorite/${encodeURIComponent(placeID)}`, {
            method: 'POST'
        })
            .then(response => response.json())
            .then(isFavorite => {
                if (isFavorite) {
                    // 이미 즐겨찾기에 추가된 경우
                    console.log("check:fav");
                    favImg.src = '/img/map/yellowStar.png';
                } else {
                    // 즐겨찾기에 추가되지 않은 경우
                    console.log("check:not fav");
                    favImg.src = '/img/map/whiteStar.png';
                }
            })
            .catch(error => {
                console.error('즐겨찾기 확인 중 오류 발생:', error);
            });
    }

    //게시글 목록 불러오기
    function getList(placeID, currentPage, pageSize, isFetching) {
        if (isFetching) return;  // 이미 데이터 요청 중이면 중단
        isFetching = true;

        fetch(`/board/list/${encodeURIComponent(placeID)}?page=${currentPage}&size=${pageSize}`, {
            method: 'GET'
        })
            .then(response => response.json())
            .then(boardList => {

                console.log(boardList);

                if (boardList && boardList.length > 0) {
                    let boardHtml = '';
                    boardList.forEach(boardItem => {
                        boardHtml += `<li>${boardItem.title} - ${boardItem.contents}<br>`;

                        // 사진이 있는 경우
                        if (boardItem.pictures && boardItem.pictures.length > 0) {
                            boardItem.pictures.forEach(picture => {
                                boardHtml += `<img src="${picture.path}" alt="${picture.oriFilename}" loading="lazy" width="100"><br>`;
                            });
                        }

                        boardHtml += `</li>`;
                    });

                    board.innerHTML += boardHtml;  // 게시글 추가

                }
            })
            .catch(error => console.error('오류 발생:', error))
            .finally(() => {
                isFetching = false;  // 데이터 로딩 완료
            });
    }

    //맵 생성
    function initMap() {
    //getCurrentPosition에서 값을 받으면 position으로 값이 들어간다.
    //position에는 coords.을 통해 latitude, longitude등의 정보를 담고있다.
    //geolocation은 비동기함수 이므로 아래의 맵을 함께 포함시키지 않으면 좌표가 없는 상태로 실행된다.
    //비동기는 다른 함수와 동시에 실행될 수 있도록 하는 함수 따라서 비동기로 되어있기에 좌표구하는 함수가 실행중에 다른 함수도 실행이 실행된다.
    navigator.geolocation.getCurrentPosition((position) => {
        // 현재 위치로부터 위도 경도 추출
        // myLat = position.coords.latitude;
        // myLong = position.coords.longitude;

        myLat = 35.68123525218729;
        myLong = 139.76714259173016;

        let centerPosition = {lat: myLat, lng: myLong};

        // id가 map인 html 요소에 새로운 map 객체 생성
        map = new google.maps.Map(document.getElementById('map'), {
            center: centerPosition,
            zoom: 18,
            scaleControl:true,
            zoomControl: true,
            mapId:'d506da1b6acddc31',
            mapTypeControl:false
            // minZoom:15,                 //지도 200m까지 축소
        });   //맵 지정 및 맵 옵션 설정

        const shareId = getLatLngFromUrl();
        if(shareId){
            //alert(shareId);
            searchPlaceByPlaceId(shareId);
        }

        // myMarker = new google.maps.Marker({
        //     map: map,
        //     title: "myLocation",
        //     position : centerPosition,
        //     icon: {
        //         url: '/img/myMarker.png', // 사용자 정의 아이콘 URL
        //         scaledSize: new google.maps.Size(50, 50), // 아이콘의 크기 조정
        //         origin: new google.maps.Point(0, 0), // 아이콘의 원점
        //         anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
        //     }
        // });
        // 현위치 바운스 애니메이션 설정
        createMarker(map,centerPosition,false,"myMarker");


        //맵 클릭 이벤트 (패널정보 none)
        google.maps.event.addListener(map,'click',function(){
            if(isPanelVisible){
                let infoPanel = document.getElementById("info-panel");
                infoPanel.style.display = 'none';
                isPanelVisible = false;
            }
        });

        //병원 버튼 클릭 시 근처 500m 검색
        document.getElementById('hospitalFilterButton').addEventListener('click', function() {
            searchNearbyHospitals(); // 병원 검색 함수 호출
        });
        //대피소 클릭 시 검색
        document.getElementById('shelterFilterButton').addEventListener('click', function (){
            fetchNearbyShelters();
        });
        // 현재 위치 중심으로 줌 조정
        document.getElementById('myLocation').addEventListener('click',function (){
            setCurrentZoom(centerPosition);
        });
        // 즐겨찾기 버튼
        favBtn = document.getElementById('favMarker');
        favImg = favBtn.querySelector('img'); // 버튼 내부의 이미지 요소 선택

        favBtn.addEventListener('click', function () {
            // 이미지 변경
            if (favImg.src.includes('whiteStar.png')) {
                favImg.src = '/img/map/yellowStar.png';
                favImg.style.width = "50px";  // 기존 크기 지정
                favImg.style.height = "50px"; // 기존 크기 지정
                favMarker(currentMarker.placeId);// 즐겨찾기 추가
            } else {
                favImg.src = '/img/map/whiteStar.png';
                favImg.style.width = "50px";  // 기존 크기 지정
                favImg.style.height = "50px"; // 기존 크기 지정
                favMarkerRemove(currentMarker.placeId);
            }
        });

        // 검색창 search-input 에서 사용자가 입력한 값을 받아 저장
        const input = document.getElementById('search-input');              //search-input 에서 값을 받아 저장
        // places api의 searchbox에 사용자가 입력한 input 데이터 입력(엔터나 검색버튼을 누르지 않아도 자동으로 뜨는 항목들)
        const searchBox = new google.maps.places.SearchBox(input);          //places api의 searchbox에 input 데이터 입력

        // 엔터를 누르는 등 폼을 제출하는 이벤트가 발생했을 시
        document.querySelector('#search_form').addEventListener('submit', function(event) {
            event.preventDefault(); // 폼 제출 시 페이지 새로고침 방지
            // 여기에서 검색 작업 수행
            google.maps.event.trigger(searchBox, 'places_changed'); // 검색 트리거 아래의 listener(places_changed)를 호출
        });

        //검색 상자에서 places_changed를 통해 실행되는 이벤트 리스너
        searchBox.addListener('places_changed', () => {
            //places_changed는 검색창에 엔터나 목록에서 장소 선택시 이벤트 발생
            const places = searchBox.getPlaces();       //places에는 검색장소 목록이 배열로 저장됨

            if (places.length == 0) {                   //places.length는 검색결과로 반환된 장소의 수
                return;                                 //검색장소가 0개 일 경우 return을 통해 아래 함수는 실행이 안된다.
            }
            alert("result"+places[0].name)              //검색장소의 첫번째로 나오는 장소 알림

            //기존 마커 제거
            delMarker();

            // 지도의 경계를 받아 마커표시 시 지도 바깥이면 숨김
            // const bounds = map.getBounds(); // 현재 지도 경계 가져오기
            //
            // markers.forEach(marker => {
            //     if (bounds.contains(marker.getPosition())) {     //마커가 현재 경계 안에 있는지 확인
            //         marker.setMap(map); // 경계 내에 있으면 표시
            //     } else {
            //         marker.setMap(null); // 경계 밖에 있으면 숨김
            //     }
            // });

            // 지도 중심과 마커를 새 장소로 이동
            const bounds = new google.maps.LatLngBounds();              //LatLngBounds는 지도의 경계를 얻는 객체 bounds 초기화
            places.forEach((place) => {                                                 //places배열을 place로
                if (!place.geometry || !place.geometry.location) {                            //만약 장소관련 정보가 없으면 return
                    console.log("Returned place contains no geometry");
                    return;
                }

                //검색된 장소 하나씩 마커생성 지도에 추가
                let marker = createMarker(map,place, false);
                markers.push(marker);   //배열에 마커추가

                if (place.geometry.viewport) {              //해당장소가 viewport정보를 가지고 있을 시 (넓은 지역을 차지하는 곳)
                    bounds.union(place.geometry.viewport);  //bounds와 검색된 viewport값을 합쳐 합쳐진 지도 경계설정
                } else {
                    bounds.extend(place.geometry.location); //위도 경도값을 받아 bounds값 설정
                }

            });

            map.fitBounds(bounds);  //위의 each문에서 place의 bounds값을 모두 포함하여 경계 안에 있는 모든장소들이 보이게함
        });
    });

        // 공유버튼 클릭 시
        document.getElementById("shareBtn").addEventListener('click', function() {
            if (currentMarker) {

                console.log("markerId:",currentMarker.placeId);
                //hash 기반 url 생성 encode로 placeid를 인코딩하고 id를 #으로 변환
                shareUrl = `${window.location.origin}/#${encodeURIComponent(currentMarker.placeId)}`;
                alert(shareUrl);
                navigator.clipboard.writeText(shareUrl);

            } else {
                alert("No marker selected!");
            }
        });

        // 출발버튼 클릭 시, 출발지 마커 생성
        document.getElementById("departureB").addEventListener('click', function(){
            // 출발 마커 생성
            if (departmentMarker) {
                departmentMarker.setMap(null); // 기존 출발 마커 제거
            }

            // 출발마커를 생성하고 지도에 추가
            departmentMarker = new google.maps.Marker({
                map: map,
                title: "DEPARTURE: "+currentMarker.title,
                position: currentMarker.position,
                placeId: currentMarker.placeId,                        //place의 장소번호 저장
                placePhoto: currentMarker.placePhoto,
                icon: {
                    url: '/img/map/departureMarker.png', // 사용자 정의 아이콘 URL
                    scaledSize: new google.maps.Size(50, 50), // 아이콘의 크기 조정
                    origin: new google.maps.Point(0, 0), // 아이콘의 원점(기본값, 아이콘 좌상단)
                    anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
                }
            });
            // 출발마커의 위도경도를 변수에 담음 - 경로 표시 위해
            departLat = departmentMarker.getPosition().lat();
            departLong = departmentMarker.getPosition().lng();
            console.log("departureLat: " + departLat);
            console.log("departureLong: " + departLong);

            // 출발 마커 생성 후, 경로 생성해 화면 출력 요청 함수 호출
            calculateRoutes();
        });
        // 도착버튼 클릭 시, 도착지 마커 생성
        document.getElementById("arrivalB").addEventListener('click', function(){
            // 도착 마커 생성
            if (arrivalMarker) {
                arrivalMarker.setMap(null); // 기존 도착 마커 제거
            }

            // 도착마커를 생성하고 지도에 추가
            arrivalMarker = new google.maps.Marker({
                map: map,
                title: "ARRIVAL: "+currentMarker.title,
                position: currentMarker.position,
                placeId: currentMarker.placeId,                        //place의 장소번호 저장
                placePhoto: currentMarker.placePhoto,
                icon: {
                    url: '/img/map/arrivalMarker.png', // 사용자 정의 아이콘 URL
                    scaledSize: new google.maps.Size(60, 60), // 아이콘의 크기 조정
                    origin: new google.maps.Point(0, 0), // 아이콘의 원점(기본값, 아이콘 좌상단)
                    anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
                }
            });
            arrivalLat = arrivalMarker.getPosition().lat();
            arrivalLong = arrivalMarker.getPosition().lng();
            console.log("arrivalLat: " + arrivalLat);
            console.log("arrivalLong: " + arrivalLong);

            if (departLat && departLong) {
                calculateRoutes();  // 경로 계산 함수 호출
            }        });
    }

    // 경로 요청 및 표시 함수 (도보와 자전거 2가지 경로 + 구글맵에 한국 지리가 잘 반영 안 되어 있어서, 확인용 대중교통 경로 추가)
    function calculateRoutes() {

        // 출발지나 도착지가 설정되지 않은 경우 경고 메시지 출력 후 종료
        if (!arrivalLat || !arrivalLong || !departLat || !departLong) {
            console.error("출발지 또는 도착지의 좌표가 올바르지 않습니다.");
            return;
        }

        // 경로 생성 시 사용할 위도 경도
        let lat;
        let long;

        // Directions Service와 Directions Renderer를 각각 도보와 자전거 경로용으로 설정
        const walkingService = new google.maps.DirectionsService();
        const bicyclingService = new google.maps.DirectionsService();

        // 도보 경로를 위한 Renderer 설정
        const walkingRenderer = new google.maps.DirectionsRenderer({
            polylineOptions: { strokeColor: 'green' } // 도보 경로의 색상 설정
        });
        walkingRenderer.setMap(map); // 도보 경로 표시할 지도

        // 자전거 경로를 위한 Renderer 설정
        const bicyclingRenderer = new google.maps.DirectionsRenderer({
            polylineOptions: { strokeColor: 'blue' } // 자전거 경로의 색상 설정
        });
        bicyclingRenderer.setMap(map); // 자전거 경로 표시할 지도

        // 출발지 마커를 따로 설정하지 않았을 때 - 사용자 현재위치를 기준으로 도착지까지의 경로 출력
        if (!departmentMarker) {
            lat = myLat;
            long = myLong;
        }
        else{   // 출발지 마커가 설정되어 있으면 출발지 위도 경도를 기준으로 경로 출력
            lat = departLat;
            long = departLong;
        }

        // 도보 경로 요청
        const walkingRequest = {
            origin: {lat: lat, lng: long}, // 출발 위치
            destination: {lat: arrivalLat, lng: arrivalLong}, // 도착 위치
            travelMode: google.maps.TravelMode.WALKING, // 도보
            language: 'en' // 소요시간 '분/시간'을 국제화할 수 없으므로 영어로 표시
        };

        walkingService.route(walkingRequest, function(result, status) {
            if (status === google.maps.DirectionsStatus.OK) {
                walkingRenderer.setDirections(result); // 도보 경로를 지도에 표시

                // 도보 경로 시작점에 아이콘 추가
                const startLocation = result.routes[0].legs[0].start_location;
                const walkingMarker = new google.maps.Marker({
                    position: startLocation,
                    map: map,
                    icon: {
                        url: '/img/map/walking.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(60, 60) // 아이콘 크기 설정
                    },
                    title: '도보 경로 시작점'
                });
                // 도보 경로의 거리와 소요 시간 출력
                const walkingLeg = result.routes[0].legs[0];
                const walkingInfoWindow = new google.maps.InfoWindow({
                    content: `
                      <div>
                        <h4>${walkingRoot}</h4>
                        <p>${distanceLabel}: ${walkingLeg.distance.text}</p>
                        <p>${durationLabel}: ${walkingLeg.duration.text}</p>
                      </div>
                    `
                });
                walkingInfoWindow.open(map, walkingMarker);
            } else {
                console.error('도보 경로를 찾을 수 없습니다.', status);
            }
        });

        // 자전거 경로 요청
        const bicyclingRequest = {
            origin: {lat: lat, lng: long}, // 출발 위치
            destination: {lat: arrivalLat, lng: arrivalLong}, // 도착 위치
            travelMode: google.maps.TravelMode.BICYCLING, // 자전거
            language: 'en' // 소요시간 '분/시간'을 국제화할 수 없으므로 영어로 표시
        };

        bicyclingService.route(bicyclingRequest, function(result, status) {
            if (status === google.maps.DirectionsStatus.OK) {
                bicyclingRenderer.setDirections(result); // 자전거 경로를 지도에 표시

                // 자전거 경로 시작점에 아이콘 추가
                const startLocation = result.routes[0].legs[0].start_location;
                const bicyclingMarker = new google.maps.Marker({
                    position: startLocation,
                    map: map,
                    icon: {
                        url: '/img/map/bicycle.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(60, 60) // 아이콘 크기 설정
                    },
                    title: '자전거 경로 시작점'
                });

                // 자전거 경로의 거리와 소요 시간 출력
                const bicyclingLeg = result.routes[0].legs[0];
                const bicyclingInfoWindow = new google.maps.InfoWindow({
                    content: `
                    <div>
                        <h4>${bicycleRoot}</h4>
                        <p>${distanceLabel}: ${bicyclingLeg.distance.text}</p>
                        <p>${durationLabel}: ${bicyclingLeg.duration.text}</p>
                    </div>
                `
                });
                bicyclingInfoWindow.open(map, bicyclingMarker);
            } else {
                console.error('자전거 경로를 찾을 수 없습니다.', status);
            }
        });

    }