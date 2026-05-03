<?php
include 'db.php';


$content = $conn->real_escape_string($_POST['content']); 

$sql = "INSERT INTO notes (content) VALUES ('$content')";

if ($conn->query($sql) === TRUE) {
    echo "Note added";
} else {
    
    echo "Error: " . $conn->error; 
}
?>