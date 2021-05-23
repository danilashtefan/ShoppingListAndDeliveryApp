# ShoppingListAndDeliveryApp

# Introudction
I guess everyone at least once in a life dreamed thet
there is no need to go to the shops. With my updated shopping list the dream can become true. User will just have
to press delivery button and the driver will be able to pick up their order and bring them whatever they want. But this is not
the end of the surprises, if you would like to earn some money, you will be able to become a driver and accept
other people orders. 

# User stories
- [ ] User is able to create his own shopping list. Add the product, choose product type, price and description
- [ ] User is able to mark products as purchased, delete all the products, check how many products left to be purchased
- [ ] On the main page user is able to navigate to the delivery functionality
- [ ] On the Delivey View user is able to choose whether he would like to be a customer or earch some money and be a delivery guy
- [ ] Customer can see his location on the map, place and cancel the order
- [ ] Driver is able to pick up the orders nearby, check what user has ordered and check the locations of the nearest shops 

# Architecture description
 ### Database
 ROOM local database is used to store the items of the user's shopping list and retrieve them when the application starts the next time
 Parse Server by Bitnami running in the AWS is used to manage the Delivery functionality. User roles (driver or customer), location and other info is stored there
 
### Architecture Design
<img width="915" alt="Screen Shot 2021-05-23 at 10 26 50" src="https://user-images.githubusercontent.com/57729718/119253428-829f1000-bbb1-11eb-9f0a-6253b9a2b048.png">




