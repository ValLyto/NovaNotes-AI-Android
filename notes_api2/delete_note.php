<?php
include 'db.php';

$id = $_POST['id'];

$sql = "DELETE FROM notes WHERE id = $id";

if ($conn->query($sql) === TRUE) {
    echo "Note deleted";
} else {
    echo "Error: " . $conn->error;
}
?>