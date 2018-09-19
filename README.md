# Facebox SDK

## Usage

### Get the ip address of Facebox through 'Circle Point' APP
### Change the IP address in source code
### Run the sample code

## Known Person / Stranger Events Format
### Known Person Event
```
{
    "status":"known person",
    "persons":[
        {
          "id": Recognized Person ID,
          "uuid": Facebox Device ID,
          "group_id": Group ID,
          "img_url": Recognized Face Image,
          "current_ts": Current timestamp,
          "accuracy": Accuracy,
          "fuzziness": Image Fuzziness Score
        }
    ],
    "person_id": Recognized Person ID
}
```
### Stranger Event
```
{
    "status":"Stranger",
    "persons": [
        {
            "uuid": Facebox Device ID,
            "group_id": Group ID,
            "img_url": Face Image of detected face,
            "current_ts": Current timestamp,
            "fuzziness": Image Fuzziness Score
        }
    ],"person_id": Reversed
}
```

## Language Support

### [Python](python)
### [Nodejs](nodejs)
### [Ruby On Rails](ruby)



