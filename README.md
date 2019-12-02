### This README is not up to date - see Halifax Car Rental.doxc



# Halifax Car Rental

Halifax Car Rental is an application designed for use by customers and representatives of an imagined car rental agency in Halifax. 

## Group members

| Name                   | Banner ID  | Email               |
| ---------------------- | ---------- | ------------------- |
| Megan Brock            | B00728460  | mg247961@dal.ca     |
| Samath Scott           | B00689755  | sm288145@dal.ca     |

## Description

1-3 paragraphs summarizing the purpose of the software, the background and motivation for
its creation, and any other "Big C" context elements.

### Users

Users of the application will be people who are looking to rent a car in the Halifax regional municipality (HRM). 
The targetted userbase includes people who live in or visit the HRM who are able to drive. As such, the userbase consists of users who are at least 16 years of age. 
Users are expected to be adults who are familiar with using mobile devices.

The majority of users are expected to be those who live in the HRM. These users likely do not own cars, but have some need to drive. Other users are expected to be visiting the area without their car, should they own one. 
Some users will be familiar with their area while using the application, while others will not be. This was an important aspect for the design of the application; it should provide useful directions to both novice and adept users, without being inconvenient or distracting for the adept users.


### Features
- Find fastest route to a rental car: The user can select a rental car on a map and view multiple routes to the car. The routes include the duration, and the quickest route is highlighed. The car can be rented at any time; the user can rent a car before they arrive at it's location. 
- Easily identify rental cars in the world: The application aids the user to identify rental cars. When renting a car, the user is presented with a detailed view of the cars details, including the make, model, and year of the car, as well as an image for users who may not recognize a car using those terms. Additionally, the augmented reality feature of the application superimposes location markers into the real-world in the (approximate) locations of available rental cars. 
- In-app communication with the rental agency: There is a chat system in place to allow the user to communicate using text, audio, image, and video with a representative of the rental agency. The client should be able to communicate quickly and easily with the agency, without having to remember or look up contact information.


## Libraries
Dependencies
---------------
Google

Google play services


**com.google.android.gms:play-services-auth:8.4.0**

**com.google.android.gms:play-services-maps:17.0.0**

**com.google.android.gms:play-services-location:17.0.0**


Google maps

**com.google.maps.android:android-maps-utils:0.5+**

**com.google.maps:google-maps-services:0.2.9**


Sceneform

**com.google.ar.sceneform:core:1.10.0**

**com.google.ar.sceneform.ux:sceneform-ux:1.10.0**


FireBase

**com.google.firebase:firebase-core:16.0.7**

**com.google.firebase:firebase-firestore:17.1.5**

**com.google.firebase:firebase-analytics:16.1.5**

**com.google.firebase:firebase-storage:16.0.1**

**com.google.firebase:firebase-database:16.0.1**


**com.google.android.material:material:1.0.0**


Other dependencies

**com.firebaseui:firebase-ui-firestore:4.3.1**

**org.slf4j:slf4j-nop:1.7.25**

**de.hdodenhof:circleimageview:2.2.0**

**com.xwray:groupie:2.1.0**

**com.squareup.picasso:picasso:2.71828**

**com.github.appoly:ARCore-Location:1.0.5.** Appoly ARCore Location allows objects to be placed in ARCore using geographic coordinates.  Source [here](https://github.com/appoly/ARCore-Location)

**arcore-for-all.** ARCore for all is a library that allows devices that are not supported by ARCore, yet have the appropriate hardware to run ARCore, to do so. It replaces the arcore_client library with a modified version. Source [here](https://github.com/tomthecarrot/arcore-for-all)


Images

**Kameleon Free Pack - Rounded.** Icon set by Webalys on ICONFINDER. Source [here](https://www.iconfinder.com/iconsets/kameleon-free-pack-rounded)

**Circle Icons.** Icon set by Nick Roach on ICONFINDER. Source [here](https://www.iconfinder.com/iconsets/circle-icons-1)

**Camera, dual, iphone 7 icon.** Icon by Webalys ICONFINDER. Source [here](https://cdn0.iconfinder.com/data/icons/iphone-7-airpods-icons-solid-style/24/dual-camera-2_1-128.png)

**Car Placeholder Free.** Icon by Freepik on FLATICON. Source [here](https://www.flaticon.com/free-icon/car-placeholder_75782.)

**Plus icon.** Icon on ICONFINDER. Source [here](https://cdn4.iconfinder.com/data/icons/keynote-and-powerpoint-icons/256/Plus-128.png)

**Refresh Icons.** Icons on icons8. Source [here](https://icons8.com/icons/set/refresh)

**Chat High Quality PNG.** Icon by Pyare on pngriver. Source [here](https://pngriver.com/download-chat-high-quality-png-100130/)

**Focus2move: World Best Selling Car - The top 100 in 2019.** Images on focus2move. Source [here](https://focus2move.com/world-best-selling-car/)

**Map Marker.** Model by Chris Ross on poly.google.com. Source [here](https://poly.google.com/view/6ij4nru_NPh)

**Car, list icon.** Icon by Webalys on ICONFINDER. Source [here](https://www.iconfinder.com/icons/377493/car_list_icon)

**File:Information.png.** Image on WIKIPEDIA. Source [here](https://en.wikipedia.org/wiki/File:Information.png)

**Car parking, gps, map pin, parking, placeholder icon.** Icon by Creative Stall on ICONFINDER. Source[here](https://www.iconfinder.com/icons/2124530/car_parking_gps_map_pin_parking_placeholder_icon)

**City map, home gps, home location, home map pointer, home pin icon.** Icon by Vectors Market on ICONFINDER. Source [here](https://www.iconfinder.com/icons/2699295/city_map_home_gps_home_location_home_map_pointer_home_pin_icon)

**Parking Area Sign (Left Arrow)** Image on SafetySign.com. Source [here](https://www.safetysign.com/products/8053/big-p-parking-left-sign)

**Free Refresh Icon** Icon on icons8.com. Source [here](https://icons8.com/icon/3N62NpxoF0tf/refresh)

**Acura 2020 ILX** Images on acura.com. Source [here](https://www.acura.com/ilx)

**The 2019 A5 Coupé** Images on audi.com. Source [here](https://www.audi.ca/ca/web/en/models/a5/a5-coupe-new.html)

**BMW i3** Images on WIKIPEDIA. Source [here](https://en.wikipedia.org/wiki/BMW_i3)

**HONDA Civic** Images on driving.ca. Source [here](ttps://driving.ca/honda/civic)

**Kia Cadenza** Images on guideautoweb.com. Source [here](https://www.guideautoweb.com/en/makes/kia/cadenza/2019/)


## Requirements
- Android 9.0, minimum sdk version 28.
- Camera and image capture permissions
- Audio recording permissions
- Location permissions
- Read and write to external storage permissions

## Installation Notes

Installation instructions for markers, if any extra steps are required (beyond installing the delivered APK).

To run on an emulator:
- Requires Android Studio 3.1 and Android Emulator 27.2.9 or later. 
- Development machine must support OpenGL ES 3.0 or later.
- Emulated device must be Pixel or Pixel 2, with the "Oreo: API Level 27: x86: Android 8.1" system image.
- See https://developers.google.com/ar/develop/java/emulator 

To run on a device:
- Device must be supported by ARCore. See https://developers.google.com/ar/discover/supported-devices for lists of ARCore supported devices. 

## Final Project Status

A discussion of what was accomplished and what was not.
Discuss milestones that were not accomplished and explain why they were not achieved.
What would be the next step for this project (if it were to continue)?

### Minimum Functionality
- Map with location markers (Completed)
- Ability to take photos for chat (Completed)
- Text chat communication (Completed)

### Expected Functionality
- Route tracing and cost determination (Partially Completed)
- Augmented reality car location markers (Partially Completed)
- Audio chat communication (Partially Completed)

### Bonus Functionality
- Ability to see available cars, rent and return from the map (Completed)   NOTE: expected and bonus location functionalities should have been switched
- Video chat communications (Partially Completed)
- Face recognition for application sign in (Not Implemented)


## Code Examples

You will encounter roadblocks and problems while developing your project.
Share 2-3 'problems' that your team encountered.
Write a few sentences that describe your solution and provide a code snippet/block
that shows your solution. Example:

**Problem 1: We needed to show car details from the AR activity**
```
    // the method to make a 3D model location marker and add an OnTapListener
    private fun getMarker(car: Car) : Node {
        val node = Node()
        node.renderable = modelRenderable
        node.setOnTapListener {hitTestResult, motionEvent ->
            ShowCarInfo(car)
        }
        return node
    }

    // the method to make a 2D view location marker and add an OnTapListener
    private fun getView(car: Car) : Node {
        val node = Node()
        if (car.isavaliable)
            node.renderable = availableCarLayoutRenderable
        else node.renderable = rentedCarLayoutRenderable
        node.setOnTapListener {hitTestResult, motionEvent ->
            ShowCarInfo(car)
        }
        return node
    }

    // the method to start the CarDetails activity, with the car name globally available as a key
    private fun ShowCarInfo(car: Car) {
        val intent = Intent(this, CarDetails::class.java)

        val sharedData = Globals.instance
        sharedData.car_name = car.name
        startActivity(intent)
    }
```

**Problem 2: We needed to return to the ChatList activity from the ChatLog fragment for admin users, but not for client users - as clients only have one person to talk to, they don't use the ChatList activity.**

```
// the ChatListFragment that is only used for admin users
class ChatListFragment : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity)!!.supportActionBar?.title = "Chat"

        recyclerview_user_log.adapter = adapter

        getUsers()

        val sharedData = Globals.instance
        if(sharedData.username != "admin")
        {
            sharedData.talkTo = "admin"
            goToChatLog(false)
        }
    }

    companion object {
        lateinit var fragment: Fragment
        // adds the fragment transaction to the backstack if addToBackstack is true
        // this allows us to selectively 
        fun goToChatLog(addToBackstack: Boolean) {
            val transaction = fragment!!.childFragmentManager.beginTransaction()
            val chatLog: Fragment = ChatLogFragment()
            transaction.replace(R.id.constraintLayout, chatLog)
            if (addToBackstack) {
                transaction.addToBackStack(null)
            }
            transaction.commit()
        }
    }
    ...
}
```

```
// the ChatListItem that appears in ChatListFragment
class ChatListItem(val userName: String, val userImage: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.txtName.text = userName

        val uri = userImage
        val targetImageView = viewHolder.itemView.img_Car_Img
        Picasso.get().load(uri).into(targetImageView)

        viewHolder.itemView.img_Car_Img?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                setUser()

                ChatListFragment.goToChatLog(true)
            }
        })

        viewHolder.itemView.txtName?.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View) {
                setUser()
                ChatListFragment.goToChatLog(true)
            }
        })
    }
    ...
}
```

**Problem 3: We needed the user to be able to highlight a specific route out of the many given to their destination**
```
class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnPolylineClickListener {
    // creates routes between the users location and a chosen marker on the map
    private fun calculateDirections(marker:Marker) {
        var destination = com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        )
        var directions = DirectionsApiRequest(mGeoApiContext)
        directions.alternatives(true)
        directions.origin(
                com.google.maps.model.LatLng(
                        lat,
                        lang
                )
        )
        directions.destination(destination).setCallback(object:PendingResult.Callback<DirectionsResult> {
            override fun onResult(result:DirectionsResult) {
                addPolylinesToMap(result)
            }
            ...
        })
    }
    
    ...
    
    // highlights the selected route when pressed
    override fun onPolylineClick(polyline:Polyline) {
        var index = 0
        for (polylineData in mPolyLinesData)
        {
            index++
            if (polyline.getId().equals(polylineData.polyline.getId()))
            {
                polylineData.polyline.setColor(ContextCompat.getColor(activity!!, R.color.blue1))
                polylineData.polyline.setZIndex(1f)
                val endLocation = LatLng(
                        polylineData.leg.endLocation.lat,
                        polylineData.leg.endLocation.lng
                )
                val marker = mMap.addMarker(MarkerOptions()
                        .position(endLocation)
                        .title("Route #" + index)
                        .snippet("Duration: " + polylineData.leg.duration))
                mTripMarkers.add(marker)
                marker.showInfoWindow()
            }
            else
            {
                polylineData.polyline.setColor(ContextCompat.getColor(activity!!, R.color.darkGrey))
                polylineData.polyline.setZIndex(0f)
            }
        }
    }
    ...
}
```


## Functional Decomposition

A diagram and description of the application's primary functions and decomposition.
[TODO: Identify and describe how this differs from High-level Organization.]

## High-level Organization

The hierarchy or site map of the application.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Clickstreams

A brief description of the common use cases.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Layout

Wire-frames of all the primary views and a brief description describing what each is for.
This can be reused from Updates 1 and 2, updated with any changes made since then.

## Implementation

Screenshots of all the primary views (screens) and a brief discussion of the
interactions the user performs on the screens.

## Future Work

Although the expected camera functionality, the ability to find rental cars in augmented reality (AR), 
was completed, the location markers in the AR activity are unstable. Unfortunately a solution using the 
libraries chosen to accomplish this task has not been found. As such, future work could include researching 
and testing other libraries that allow virtual items to be placed using geolocation. A base project called Geolocation-ARCore, 
by aishnacodes on GitHub [3] appears to place AR objects using location coordinates with much greater stability, as seen in a video 
posted by the projects owner on YouTube [ ].


## Sources

Use IEEE citation style. Some examples:

[1] J. Moule, _Killer UX Design: Create User Experiences to Wow Your Visitors_. Sitepoint, 2012.

[2] _Ngspice_. (2011). [Online]. Available: http://ngspice.sourceforge.net

[3] "Detect shaking of device in left or right direction in android?", StackOverflow.
    https://stackoverflow.com/a/6225656 (accessed July 12, 2019).

What to include in your project sources:
- Stock images
- Design guides
- Programming tutorials
- Research material
- Android libraries
- Everything listed on the Dalhousie [*Plagiarism and Cheating*](https://www.dal.ca/dept/university_secretariat/academic-integrity/plagiarism-cheating.html)



TODO: copy over all refs from Libraries section when finished

GitHub

[1] 
    https://github.com/appoly/ARCore-Location (accessed Nov 28, 2019).

[2]
    https://github.com/tomthecarrot/arcore-for-all (accessed Nov 28, 2019).

[3] 
    https://github.com/aishnacodes/Geolocation-ARCore 


Official Documentation

[ ]
    https://developers.google.com/ar/discover/supported-devices (accessed Nov 28, 2019).


[ ]
    https://developers.google.com/ar/develop/java/sceneform/create-renderables (accessed Nov 28, 2019).


[ ]
    https://developer.android.com/guide/navigation/navigation-custom-back (accessed Nov 28, 2019).


StackOverflow Questions

[ ] "How to get the context inside a nested class?", StackOverflow. 
    https://stackoverflow.com/questions/48952949/how-to-get-the-context-inside-a-nested-class (accessed Nov 28, 2019).

[ ] "Replacing a fragment with another fragment inside activity group", StackOverflow.
    https://stackoverflow.com/questions/5658675/replacing-a-fragment-with-another-fragment-inside-activity-group
    
[ ] "How to close the current fragment by using Button like the back button?", StackOverflow.
    https://stackoverflow.com/questions/20812922/how-to-close-the-current-fragment-by-using-button-like-the-back-button (accessed Nov 28, 2019).

[ ] "Android : Parent fragment of a nested fragment", StackOverflow.
    https://stackoverflow.com/questions/26991560/android-parent-fragment-of-a-nested-fragment (accessed Nov 28, 2019).


[ ] "Removing Fragment by clicking/touching outside", StackOverflow
    https://stackoverflow.com/questions/15323424/removing-fragment-by-clicking-touching-outside (accessed Nov 28, 2019).

[ ] "getSupportActionBar from inside of Fragment ActionBarCompat", StackOverflow
    https://stackoverflow.com/questions/18320713/getsupportactionbar-from-inside-of-fragment-actionbarcompat (accessed Nov 28, 2019).


Tutorials
[ ]
    https://www.tutlane.com/tutorial/android/android-video-player-with-examples (accessed Nov 28, 2019).
    
    
Other
[ ] 
    https://www.youtube.com/watch?v=RAg6u2AZ1fI