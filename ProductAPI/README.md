To make the docker compose work: 
mvn clean install - in this project

then use the docker compose





Swagger doesn't work when adding a perfume (can't put type) so use cmd line
curl -X POST "http://localhost:8090/api/v1/perfumes/add" \
-H "accept: application/json" \
-H "Authorization: <Token>" \
-H "Content-Type: multipart/form-data" \
-F 'perfume={
"perfumeTitle":"Acqua di Gio",
"perfumer":"Giorgio Armani",
"year":2025,
"country":"Italy",
"perfumeGender":"Unisex",
"fragranceTopNotes":"Citrus, Bergamot",
"fragranceMiddleNotes":"Jasmine, Rosemary",
"fragranceBaseNotes":"Musk, Amber",
"description":"A fresh and aquatic fragrance.",
"price":120,
"volume":50,
"type":"Eau de Toilette"
};type=application/json'
