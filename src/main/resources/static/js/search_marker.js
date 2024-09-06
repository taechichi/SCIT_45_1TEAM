    let myLat;  // 내 위치 위도
    let myLong; // 내 위치 경도
    var map;  //맵
    let myMarker;   // 내 위치 마커

    let markers = [];                  //마커배열(places 검색을 통해 나온 마커들의 배열)
    let isPanelVisible = false;     //패널 상태 false

    let shareUrl;                           //공유 Url

    let arrivalLat;     // 도착치 마커
    let arrivalLong;
    let arrivalMarker;

    let departLat;      // 출발지 마커
    let departLong;
    let departmentMarker;

    function initMap() {

    //getCurrentPosition에서 값을 받으면 position으로 값이 들어간다.
    //position에는 coords.을 통해 latitude, longitude등의 정보를 담고있다.
    //geolocation은 비동기함수 이므로 아래의 맵을 함께 포함시키지 않으면 좌표가 없는 상태로 실행된다.
    //비동기는 다른 함수와 동시에 실행될 수 있도록 하는 함수 따라서 비동기로 되어있기에 좌표구하는 함수가 실행중에 다른 함수도 실행이 실행된다.
    navigator.geolocation.getCurrentPosition((position) => {
        // 현재 위치로부터 위도 경도 추출
        myLat = position.coords.latitude;
        myLong = position.coords.longitude;

        // id가 map인 요소에 새로운 map 객체 생성
        map = new google.maps.Map(document.getElementById('map'), {
            center: { lat: myLat, lng: myLong}, // 현재 위치를 기준으로
            zoom: 18,
            scaleControl:true,
            zoomControl: true,
            mapId:'d506da1b6acddc31',   // 지도 스타일 지정
            // minZoom:15,                 //지도 200m까지 축소
        });   //맵 지정 및 맵 옵션 설정

        // 내 위치 마커 생성 후 지도에 추가
        myMarker = new google.maps.Marker({
            map: map,
            title: "myLocation",
            position: { lat: myLat, lng: myLong },
            icon: {
                url: '/img/myMarker.png', // 사용자 정의 아이콘 URL
                scaledSize: new google.maps.Size(50, 50), // 아이콘의 크기 조정
                origin: new google.maps.Point(0, 0), // 아이콘의 원점
                anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
            }
        });
        // 현위치 바운스 애니메이션 설정
        myMarker.setAnimation(google.maps.Animation.BOUNCE);

        // 검색창 search-input 에서 사용자가 입력한 값을 받아 저장
        const input = document.getElementById('search-input');
        // places api의 searchbox에 사용자가 입력한 input 데이터 입력(엔터나 검색버튼을 누르지 않아도 자동으로 뜨는 항목들)
        const searchBox = new google.maps.places.SearchBox(input);

        // 엔터를 누르는 등 폼을 제출하는 이벤트가 발생했을 시
        document.querySelector('#search_form').addEventListener('submit', function(event) {
            // 폼 제출 시 페이지 새로고침 방지
            event.preventDefault(); 
            // 여기에서 검색 작업 수행
            // 검색 트리거 아래의 listener(places_changed)를 호출
            google.maps.event.trigger(searchBox, 'places_changed'); 
        });       


        // 검색 상자에서 places_changed를 통해 실행되는 이벤트 리스너
        searchBox.addListener('places_changed', () => {
            // places_changed는 검색창에 엔터나 목록에서 장소 선택시 이벤트 발생
            const places = searchBox.getPlaces();       // places에는 검색장소 목록이 배열로 저장됨

            if (places.length == 0) {                   // places.length는 검색결과로 반환된 장소의 수
                return;                                 // 검색장소가 0개(검색 결과 없을 때) return을 통해 아래 함수는 실행 안됨
            }
            alert("result"+places[0].name)              // 검색장소의 첫번째로 나오는(가장 신뢰성 높은) 장소 알림

            // 전체 장소 이름을 알림으로 띄우기
            // let placeNames = places.map(place => place.name).join(", ");     //map메서드는 기존배열을 변경하지 않고 변환 값들로 이루어진 새배열을 반환
            // alert("검색된 장소들: " + placeNames);                             //place는 배열의 장소객체 name은 장소객체의 속성 반환값은 장소의이름, 으로 배열반환

            // 기존 마커 제거
            markers.forEach(marker => marker.setMap(null));   //markers배열에서 가져온 기존마커 null로 맵에서 삭제
            markers = [];   //마커 배열 초기화

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
            // LatLngBounds는 지도의 경계를 얻는 객체로, bounds 초기화
            const bounds = new google.maps.LatLngBounds();
            places.forEach((place) => {                                 // places배열 내 place 반복
                if (!place.geometry || !place.geometry.location) {      // 만약 장소관련 정보가 없으면 return
                    console.log("Returned place contains no geometry");
                    return;
                }

                // 새 마커를 생성하고 지도에 추가
                let marker = new google.maps.Marker({
                    map: map,
                    title: place.name,
                    position: place.geometry.location,
                    placeId: place.place_id,                        //place의 장소번호 저장
                    placePhoto: place.photos
                }); //마커의 옵션들은 comment의 marker_option 참조

                //마커 클릭 이벤트 (패널 정보 block)
                google.maps.event.addListener(marker, 'click', function() {
                        const placeId = marker.get("placeId");
                        // 1. 기본 장소 정보 표시
                        let placeName = marker.title;
                        let photoUrl = "";
                        photoUrl = place.photos[0].getUrl();    //place의 첫사진을 url을 받아 저장
                        let placeInfo = `<h3>${placeName}</h3>                  <!--- infopanel에 넣을 값 ---!>  
                                              <div style="width: 100%;height: 200px">
                                              <img src="${photoUrl}" style="width: 100%; height: 100%;"></div>
                                              <p>추가입력예정</p>`;
                        let infoPanel = document.getElementById("info-panel");
                        let infoPart = document.getElementById("info_part");
                        infoPart.innerHTML = placeInfo;
                        infoPanel.style.display = 'block';
                        isPanelVisible = true;

                        //공유 버튼 클릭 시  shareUrl에 url생성 해당 url애눈 좌표값을 포함
                        let btn = document.getElementById("shareBtn");
                         btn.addEventListener('click', function() {
                             shareUrl = `${window.location.origin}/map/${marker.getPosition().lat()}/${marker.getPosition().lng()}`;
                            // $window.location.origin은 현재 도메인주소를 반환, map/는 임의경로,
                            alert(shareUrl);  // URL을 확인하려면 알림으로 표시
                        });


                    // 출발버튼 클릭 시, 출발지 마커 생성
                    let dpB = document.getElementById("departureB");
                    dpB.addEventListener('click', function(){
                        // 출발 마커 생성
                        if (departmentMarker) {
                            departmentMarker.setMap(null); // 기존 출발 마커 제거
                        }

                        // 출발마커를 생성하고 지도에 추가
                        departmentMarker = new google.maps.Marker({
                            map: map,
                            title: "출발지: "+place.name,
                            position: place.geometry.location,
                            placeId: place.place_id,                        //place의 장소번호 저장
                            placePhoto: place.photos,
                            icon: {
                                url: '/img/departureMarker.png', // 사용자 정의 아이콘 URL
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
                    })
                    
                    // 도착버튼 클릭 시, 도착지 마커 생성
                    let arB = document.getElementById("arrivalB");
                    arB.addEventListener('click', function(){
                        // 도착 마커 생성
                        if (arrivalMarker) {
                            arrivalMarker.setMap(null); // 기존 도착 마커 제거
                        }

                        // 도착마커를 생성하고 지도에 추가
                        arrivalMarker = new google.maps.Marker({
                            map: map,
                            title: "도착지: "+place.name,
                            position: place.geometry.location,
                            placeId: place.place_id,                        //place의 장소번호 저장
                            placePhoto: place.photos,
                            icon: {
                                url: '/img/arrivalMarker.png', // 사용자 정의 아이콘 URL
                                scaledSize: new google.maps.Size(60, 60), // 아이콘의 크기 조정
                                origin: new google.maps.Point(0, 0), // 아이콘의 원점(기본값, 아이콘 좌상단)
                                anchor: new google.maps.Point(25, 50) // 아이콘의 앵커 포인트
                            }
                        });

                        arrivalLat = arrivalMarker.getPosition().lat();
                        arrivalLong = arrivalMarker.getPosition().lng();
                        console.log("arrivalLat: " + arrivalLat);
                        console.log("arrivalLong: " + arrivalLong);

                        calculateRoutes(); // 경로 계산 호출

                    })
                    
                });

                // 상세 정보 블럭 요소 외 빈 맵 공간 클릭 이벤트 (패널정보 none)
                google.maps.event.addListener(map,'click',function(){
                    if(isPanelVisible){
                        let infoPanel = document.getElementById("info-panel");
                        infoPanel.style.display = 'none';
                        isPanelVisible = false;
                    }
                });

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
}
    google.maps.event.addDomListener(window, 'load', initMap);      //domlistener initmap을 실행



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
            travelMode: google.maps.TravelMode.WALKING // 도보
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
                        url: '/img/walking.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(60, 60) // 아이콘 크기 설정
                    },
                    title: '도보 경로 시작점'
                });
                // 도보 경로의 거리와 소요 시간 출력
                const walkingLeg = result.routes[0].legs[0];
                const walkingInfoWindow = new google.maps.InfoWindow({
                    content: `
                    <div>
                        <h4>도보 경로 정보</h4>
                        <p>거리: ${walkingLeg.distance.text}</p>
                        <p>소요 시간: ${walkingLeg.duration.text}</p>
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
            travelMode: google.maps.TravelMode.BICYCLING // 자전거
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
                        url: '/img/bicycle.png', // 사용자 정의 아이콘 URL
                        scaledSize: new google.maps.Size(60, 60) // 아이콘 크기 설정
                    },
                    title: '자전거 경로 시작점'
                });

                // 자전거 경로의 거리와 소요 시간 출력
                const bicyclingLeg = result.routes[0].legs[0];
                const bicyclingInfoWindow = new google.maps.InfoWindow({
                    content: `
                    <div>
                        <h4>자전거 경로 정보</h4>
                        <p>거리: ${bicyclingLeg.distance.text}</p>
                        <p>소요 시간: ${bicyclingLeg.duration.text}</p>
                    </div>
                `
                });
                bicyclingInfoWindow.open(map, bicyclingMarker);
            } else {
                console.error('자전거 경로를 찾을 수 없습니다.', status);
            }
        });
    }