<?php
include 'db.php';

$id = (int)$_POST['id']; 
$content = $conn->real_escape_string($_POST['content']);

$sql = "UPDATE notes SET content = '$content' WHERE id = $id";

if ($conn->query($sql) === TRUE) {
    echo "Note updated";
} else {
    echo "Error: " . $conn->error;
}
?>