<?php
include 'db.php';
$result = $conn->query("SELECT * FROM notes ORDER BY id DESC"); 
$notes = array();
while ($row = $result->fetch_assoc()) {
    $notes[] = $row;
}
echo json_encode($notes); 
?>