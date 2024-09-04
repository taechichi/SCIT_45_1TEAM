
    let lat;  //위도
    let long; //경도
    var map;  //맵
    let markers = []; //마커배열
    let mylocation;
    let isPanelVisible = false;     //패널 상태 false

    function initMap() {

    //getCurrentPosition에서 값을 받으면 position으로 값이 들어간다.
    //position에는 coords.을 통해 latitude, longitude등의 정보를 담고있다.
    //geolocation은 비동기함수 이므로 아래의 맵을 함께 포함시키지 않으면 좌표가 없는 상태로 실행된다.
    //비동기는 다른 함수와 동시에 실행될 수 있도록 하는 함수 따라서 비동기로 되어있기에 좌표구하는 함수가 실행중에 다른 함수도 실행이 실행된다.
    navigator.geolocation.getCurrentPosition((position) => {
        lat = position.coords.latitude;
        long = position.coords.longitude;

        map = new google.maps.Map(document.getElementById('map'), {
            center: { lat: lat, lng: long},
            zoom: 18,
            scaleControl:true,
            zoomControl: true,
            mapId:'d506da1b6acddc31',
            // minZoom:15,                 //지도 200m까지 축소
        });   //맵 지정 및 맵 옵션 설정


        const input = document.getElementById('search-input');              //search-input 에서 값을 받아 저장
        const searchBox = new google.maps.places.SearchBox(input);          //places api의 searchbox에 input 데이터 입력

        document.querySelector('#search_form').addEventListener('submit', function(event) {
            event.preventDefault(); // 폼 제출 시 페이지 새로고침 방지
            // 여기에서 검색 작업 수행
            google.maps.event.trigger(searchBox, 'places_changed'); // 검색 트리거 아래의 listener(places_changed)를 호출
        });         //


        //검색 상자에서 places_changed를 통해 실행되는 이벤트 리스너
        searchBox.addListener('places_changed', () => {
            //places_changed는 검색창에 엔터나 목록에서 장소 선택시 이벤트 발생
            const places = searchBox.getPlaces();       //places에는 검색장소 목록이 배열로 저장됨

            if (places.length == 0) {                   //places.length는 검색결과로 반환된 장소의 수
                return;                                 //검색장소가 0개 일 경우 return을 통해 아래 함수는 실행이 안된다.
            }
            alert("result"+places[0].name)              //검색장소의 첫번째로 나오는 장소 알림

            // // 전체 장소 이름을 알림으로 띄우기
            // let placeNames = places.map(place => place.name).join(", ");     //map메서드는 기존배열을 변경하지 않고 변환 값들로 이루어진 새배열을 반환
            // alert("검색된 장소들: " + placeNames);                             //place는 배열의 장소객체 name은 장소객체의 속성 반환값은 장소의이름, 으로 배열반환

            //기존 마커 제거
            markers.forEach(marker => marker.setMap(null));    //markers배열에서 가져온 기존마커 null로 맵에서 삭제
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
            const bounds = new google.maps.LatLngBounds();              //LatLngBounds는 지도의 경계를 얻는 객체 bounds 초기화
            places.forEach((place) => {                                 //places배열을 place로
                if (!place.geometry || !place.geometry.location) {      //만약 장소관련 정보가 없으면 return
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
                });

                //맵 클릭 이벤트 (패널정보 none)
                google.maps.event.addListener(map,'click',function(){
                    if(isPanelVisible){
                        let infoPanel = document.getElementById("info-panel");
                        infoPanel.style.display = 'none';
                        isPanelVisible = false;
                    }
                });

                let btn = document.getElementById("shareBtn");
                btn.addEventListener('click', function (){
                        alert("aa");
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
